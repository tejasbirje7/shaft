package org.shaft.administration.marketingengine.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.marketingengine.constants.CampaignConstants;
import org.shaft.administration.marketingengine.constants.CampaignLogs;
import org.shaft.administration.marketingengine.dao.CampaignDao;
import org.shaft.administration.marketingengine.entity.CampaignCriteria.CampaignCriteria;
import org.shaft.administration.marketingengine.entity.EventRequest.EventMetadata;
import org.shaft.administration.marketingengine.repositories.CampaignRepository;
import org.shaft.administration.obligatory.campaigns.service.QueryConstructor;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CampaignService implements CampaignDao {
  private final CampaignRepository campaignRepository;
  public ObjectMapper mapper;
  private final QueryConstructor queryConstructor;
  private final ShaftQueryTranslator queryTranslator;
  public final ObjectReader objectNodeParser;
  private final ObjectNode EMPTY_OBJECT_NODE;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

  @Autowired
  public CampaignService(CampaignRepository campaignRepository) {
    this.campaignRepository = campaignRepository;
    this.queryConstructor = new QueryConstructor();
    this.mapper = new ObjectMapper();
    this.EMPTY_OBJECT_NODE = mapper.createObjectNode();
    this.queryTranslator = new ShaftQueryTranslator();
    this.objectNodeParser = new ObjectMapper().readerFor(ObjectNode.class);
  }

  @Override
  public Mono<ObjectNode> checkIfCampaignExistsForEvent(int accountId,Map<String,Object> request) {
    EventMetadata eventMetadata = mapper.convertValue(request.get(CampaignConstants.EVENT), EventMetadata.class);
    return campaignRepository.checkIfCampaignExistsForEvent(accountId, eventMetadata.getEid())
      .collectList()
      .mapNotNull(campaignsForEvent -> {
        if(!campaignsForEvent.isEmpty()) {
          List<Map<String,Object>> campaigns = mapper.convertValue(campaignsForEvent,List.class);
          int identity = Integer.parseInt((String) request.get(CampaignConstants.IDENTITY));
          ObjectNode queries = queryConstructor.constructMsearchQuery(campaigns,request, identity,accountId + "_*");
          ObjectNode queryResponse = campaignRepository.checkEligibleCampaignsForI(accountId,queries.get(CampaignConstants.QUERIES).asText());
          ObjectNode campaignToRender = checkCampaignToRender(queryResponse, (ArrayNode) queries.get(CampaignConstants.CID_MAP));
          if(!campaignToRender.isEmpty()) {
            return ShaftResponseBuilder.buildResponse(ShaftResponseCode.CAMPAIGNS_TO_RENDER,campaignToRender);
          }
        }
        return ShaftResponseBuilder.buildResponse(ShaftResponseCode.CAMPAIGNS_TO_RENDER);
      })
      .onErrorResume(t -> {
        log.error("Campaign Qualification Exception{} for account {}",t,accountId);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_CHECK_CAMPAIGN_QUALIFICATION));
      });
  }

  private ObjectNode checkCampaignToRender(ObjectNode queryResponse,ArrayNode cidMap) {
    ObjectNode campaignToRender = EMPTY_OBJECT_NODE;
    int t = 0;
    for (int i = 0; i < cidMap.size(); i++) {
      if(cidMap.get(i).has(CampaignConstants.CAMPAIGN_ID) && cidMap.get(i).get(CampaignConstants.CAMPAIGN_ID).asInt() > t) {
        if(queryResponse.has(CampaignConstants.ES_RESPONSES) && queryResponse.get(CampaignConstants.ES_RESPONSES).size() > i) {
          if(!queryResponse.get(CampaignConstants.ES_RESPONSES).get(i).get(CampaignConstants.ES_HITS).get(CampaignConstants.ES_HITS).isEmpty()) {
            campaignToRender = (ObjectNode) cidMap.get(i);
            t = cidMap.get(i).get(CampaignConstants.CAMPAIGN_ID).asInt();
          }
        } else {
          campaignToRender = (ObjectNode) cidMap.get(i);
          t = cidMap.get(i).get(CampaignConstants.CAMPAIGN_ID).asInt();
        }
      }
    }
    return campaignToRender;
  }

  @Override
  public Mono<ObjectNode> saveCampaign(int accountId, ObjectNode requestObject, FilePart image) {
    ObjectNode q = (ObjectNode) requestObject.get(CampaignConstants.QUERY);
    ObjectNode te = (ObjectNode) requestObject.get(CampaignConstants.TRIGGERING_EVENT);
    // #TODO Check if campaigns already exists for event `te` above limit
    if(q.get("whoDid" ).isEmpty() && q.get("didNot").isEmpty() && q.get("commonProp").isEmpty()) {
      requestObject.put(CampaignConstants.QUERY,"");
      requestObject.set("fs",EMPTY_OBJECT_NODE);
    } else {
      ObjectNode elasticQuery = translateRawQuery(q);
      String encodedQuery;
      try {
        String stringQuery = mapper.writeValueAsString(elasticQuery);
        byte[] bytesEncoded = Base64.encodeBase64(stringQuery.getBytes());
        encodedQuery = new String(bytesEncoded);
        requestObject.put(CampaignConstants.QUERY,encodedQuery);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
    ObjectNode modifiedRequestDto = addRequiredBackendFlags(requestObject);
    // #TODO convert this requestObject to CampaignCriteria for safety checks i.e. verify if campaign object is populated completely
    CampaignCriteria cc = mapper.convertValue(modifiedRequestDto,CampaignCriteria.class); // #TODO Handle parsing exception
    return campaignRepository.save(accountId,cc)
      .flatMap(k -> saveAssets(image))
      .onErrorResume(t -> {
        if(isRestStatusException(t)) {
          return saveAssets(image);
        } else {
          log.error(CampaignLogs.FAILED_TO_SAVE_CAMPAIGN,t.getMessage(),accountId);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_SAVE_CAMPAIGN));
        }
      });
  }

  @Override
  public Flux<ObjectNode> qualifyUsersForCampaign(int accountId, Map<String,Object> request) {
    CampaignCriteria cc = mapper.convertValue(request,CampaignCriteria.class);
    byte[] decodedBytesQuery = Base64.decodeBase64(cc.getQ());
    String query = new String(decodedBytesQuery, StandardCharsets.UTF_8);

    return campaignRepository.getPaginatedQueryResults(accountId,query,0)
      .expand(firstResponse -> {
        if(firstResponse.has("hits")) {
          ArrayNode firstBatchHits = (ArrayNode) firstResponse.get("hits").get("hits");
          if(!firstBatchHits.isEmpty()) {
            queueUsersForSendingCampaign(firstResponse);
            ArrayNode lastI = (ArrayNode) firstBatchHits.get(firstBatchHits.size()-1).get("sort");
            int searchAfter = lastI.get(0).asInt();
            return campaignRepository.getPaginatedQueryResults(accountId,query, searchAfter);
          }
        }
        return Flux.empty();
      })
      .repeat()
      .takeWhile(nextResp -> {
        if(nextResp.has("hits")) {
          ArrayNode nextBatchHits = (ArrayNode) nextResp.get("hits").get("hits");
          return !nextBatchHits.isEmpty();
        }
        return false;
      });
  }

  private void queueUsersForSendingCampaign(ObjectNode usersInBatch) {
    log.info("First Batch {}",usersInBatch);
    /*
    {"took":0,"timed_out":false,"_shards":{"total":2,"successful":2,"skipped":1,"failed":0},"hits":{"total":{"value":44,"relation":"eq"},"max_score":null,"hits":[{"_index":"1600_1710174530","_id":"XXUOUo4B_ibG4UV0Vdqy","_score":null,"_source":{"e":[{"eid":0,"name":"Babycorn","category":"Vegetables","quantity":10,"costPrice":2116,"ts":1690464167,"fp":"bXX51kp5vhKtXisD"},{"eid":4,"name":"Custard Apple Pulp","category":"Fruits","quantity":4,"costPrice":5395,"ts":1697485909,"fp":"bXX51kp5vhKtXisD"},{"eid":1,"name":"Chopped Methi","category":"Vegetables","quantity":13,"costPrice":3177,"ts":1700824591,"fp":"bXX51kp5vhKtXisD"},{"eid":2,"name":"Chopped Methi","category":"Vegetables","quantity":15,"costPrice":1028,"ts":1695607678,"fp":"bXX51kp5vhKtXisD"},{"eid":3,"name":"Spinach","category":"Vegetables","quantity":3,"costPrice":9673,"ts":1689379547,"fp":"bXX51kp5vhKtXisD"},{"eid":1,"name":"Muskmelon","category":"Fruits","quantity":5,"costPrice":7311,"ts":1690891402,"fp":"bXX51kp5vhKtXisD"},{"eid":4,"name":"Pomegranate","category":"Fruits","quantity":8,"costPrice":1896,"ts":1690779070,"fp":"bXX51kp5vhKtXisD"},{"eid":1,"name":"Chopped Methi","category":"Vegetables","quantity":3,"costPrice":3241,"ts":1706075657,"fp":"bXX51kp5vhKtXisD"},{"eid":4,"name":"Peeled Garlic","category":"Vegetables","quantity":3,"costPrice":929,"ts":1693200034,"fp":"bXX51kp5vhKtXisD"},{"eid":4,"name":"Pumpkin","category":"Vegetables","quantity":4,"costPrice":275,"ts":1691886462,"fp":"bXX51kp5vhKtXisD"}],"i":1684407743,"props":{"email":"tejas@clevertap.com","contact":8591333071}},"sort":[1684407743]},{"_index":"1600_1710174530","_id":"hXUOUo4B_ibG4UV0V9ow","_score":null,"_source":{"e":[{"eid":3,"name":"Pumpkin","category":"Vegetables","quantity":12,"costPrice":1580,"ts":1704892883,"fp":"5cIWMtQWwNTAuDDV"},{"eid":0,"name":"Tender Coconut","category":"Fruits","quantity":15,"costPrice":6238,"ts":1695980529,"fp":"5cIWMtQWwNTAuDDV"},{"eid":4,"name":"Grated coconut","category":"Vegetables","quantity":1,"costPrice":5012,"ts":1689682624,"fp":"5cIWMtQWwNTAuDDV"},{"eid":1,"name":"Chopped Methi","category":"Vegetables","quantity":10,"costPrice":5432,"ts":1702941125,"fp":"5cIWMtQWwNTAuDDV"},{"eid":2,"name":"Peeled Garlic","category":"Vegetables","quantity":2,"costPrice":4350,"ts":1686366992,"fp":"5cIWMtQWwNTAuDDV"},{"eid":2,"name":"Pumpkin","category":"Vegetables","quantity":13,"costPrice":2621,"ts":1690777802,"fp":"5cIWMtQWwNTAuDDV"},{"eid":2,"name":"Muskmelon","category":"Fruits","quantity":4,"costPrice":2602,"ts":1693578461,"fp":"5cIWMtQWwNTAuDDV"},{"eid":0,"name":"Grated coconut","category":"Vegetables","quantity":5,"costPrice":490,"ts":1688922547,"fp":"5cIWMtQWwNTAuDDV"},{"eid":3,"name":"Custard Apple Pulp","category":"Fruits","quantity":7,"costPrice":5363,"ts":1706794741,"fp":"5cIWMtQWwNTAuDDV"},{"eid":0,"name":"Grated coconut","category":"Vegetables","quantity":19,"costPrice":858,"ts":1704337372,"fp":"5cIWMtQWwNTAuDDV"}],"i":1684439686,"props":{"email":"tejas@clevertap.com","contact":8355925240}},"sort":[1684439686]},{"_index":"1600_1710174530","_id":"oHUOUo4B_ibG4UV0WNow","_score":null,"_source":{"e":[{"eid":4,"name":"Custard Apple Pulp","category":"Fruits","quantity":8,"costPrice":9718,"ts":1687050483,"fp":"OwAN3OOS3dEG5GJK"},{"eid":1,"name":"Pomegranate","category":"Fruits","quantity":9,"costPrice":3513,"ts":1686126481,"fp":"OwAN3OOS3dEG5GJK"},{"eid":3,"name":"Custard Apple Pulp","category":"Fruits","quantity":10,"costPrice":3503,"ts":1696848098,"fp":"OwAN3OOS3dEG5GJK"},{"eid":1,"name":"Pomegranate","category":"Fruits","quantity":3,"costPrice":2145,"ts":1698129946,"fp":"OwAN3OOS3dEG5GJK"},{"eid":2,"name":"Peeled Garlic","category":"Vegetables","quantity":15,"costPrice":2575,"ts":1698490673,"fp":"OwAN3OOS3dEG5GJK"},{"eid":4,"name":"Chopped Methi","category":"Vegetables","quantity":5,"costPrice":884,"ts":1705187467,"fp":"OwAN3OOS3dEG5GJK"},{"eid":4,"name":"Muskmelon","category":"Fruits","quantity":9,"costPrice":2346,"ts":1687380024,"fp":"OwAN3OOS3dEG5GJK"},{"eid":3,"name":"Muskmelon","category":"Fruits","quantity":4,"costPrice":4606,"ts":1703168714,"fp":"OwAN3OOS3dEG5GJK"},{"eid":0,"name":"Pumpkin","category":"Vegetables","quantity":12,"costPrice":9051,"ts":1692961267,"fp":"OwAN3OOS3dEG5GJK"},{"eid":0,"name":"Babycorn","category":"Vegetables","quantity":9,"costPrice":9988,"ts":1707333613,"fp":"OwAN3OOS3dEG5GJK"}],"i":1685233837,"props":{"email":"tejas@clevertap.com","contact":8591333071}},"sort":[1685233837]},{"_index":"1600_1710174530","_id":"QnUOUo4B_ibG4UV0VNq0","_score":null,"_source":{"e":[{"eid":0,"name":"Grated coconut","category":"Vegetables","quantity":8,"costPrice":4943,"ts":1703605473,"fp":"Ch7DHkbpEg4F3Njx"},{"eid":1,"name":"Pomegranate","category":"Fruits","quantity":12,"costPrice":3449,"ts":1690304664,"fp":"Ch7DHkbpEg4F3Njx"},{"eid":1,"name":"Custard Apple Pulp","category":"Fruits","quantity":3,"costPrice":7380,"ts":1692020771,"fp":"Ch7DHkbpEg4F3Njx"},{"eid":2,"name":"Custard Apple Pulp","category":"Fruits","quantity":13,"costPrice":6286,"ts":1697429026,"fp":"Ch7DHkbpEg4F3Njx"}],"i":1685383493,"props":{"email":"tejas@clevertap.com","contact":8097036598}},"sort":[1685383493]}]}}
     */

  }

  @Override
  public Mono<ObjectNode> getCampaigns(int accountId, ObjectNode requestObject) {
    return campaignRepository.getSavedCampaigns(accountId)
      .collectList()
      .map(campaigns -> {
        log.info("Campaigns : {}",campaigns);
        return ShaftResponseBuilder.buildResponse(
          ShaftResponseCode.CAMPAIGNS_RETRIEVED,mapper.valueToTree(campaigns));
      })
      .onErrorResume(error -> Mono.just(ShaftResponseBuilder.buildResponse(
        ShaftResponseCode.FAILED_TO_FETCH_SAVED_CAMPAIGNS)));
  }

  @Override
  public Mono<ObjectNode> getActivePBSCampaigns(int accountId) {
    return campaignRepository.getActivePBSCampaigns(accountId)
      .collectList()
      .map(campaigns -> {
        return ShaftResponseBuilder.buildResponse(
          ShaftResponseCode.CAMPAIGNS_RETRIEVED,mapper.valueToTree(campaigns));
      })
      .onErrorResume(error -> Mono.just(ShaftResponseBuilder.buildResponse(
        ShaftResponseCode.FAILED_TO_FETCH_SAVED_CAMPAIGNS)));
  }

  @Override
  public Mono<ObjectNode> updateCampaignStatus(int accountId,int campaignId) {
    return campaignRepository.updateCampaignStatus(accountId,campaignId,CampaignConstants.STATUS_RUNNING)
      .map(updated -> {
        if(updated > 0L) {
          return ShaftResponseBuilder.buildResponse("S");
        } else  {
          return ShaftResponseBuilder.buildResponse("F");
        }
      }).onErrorResume(exception -> {
        log.error("Exception updating campaign status {}",exception.getMessage());
        return Mono.just(ShaftResponseBuilder.buildResponse("F"));
      });
  }

  public Mono<ObjectNode> saveAssets(FilePart image) {
    ACCOUNT_ID.remove();
    log.info("File name: {}", image.filename());
    return image.transferTo(new File("/opt/shop_assets/1600/campaigns",image.filename()))
      .map(r -> ShaftResponseBuilder.buildResponse(ShaftResponseCode.CAMPAIGN_SAVED))
      .onErrorResume(error -> {
        ACCOUNT_ID.remove();
        log.error("Exception saving campaign",error);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_SAVE_CAMPAIGN));
      });
  }

  public ObjectNode addRequiredBackendFlags(ObjectNode requestObject) {
    requestObject.put(CampaignConstants.STATUS,CampaignConstants.STATUS_SCHEDULED);
    requestObject.put(CampaignConstants.IS_SPAWNED,false);
    return requestObject;
  }

  private ObjectNode translateRawQuery(ObjectNode rawQuery) {
    return queryTranslator.translateToElasticQuery(rawQuery,false);
  }
  private boolean isRestStatusException(Throwable t) {
    return t instanceof RestStatusException;
  }

}
