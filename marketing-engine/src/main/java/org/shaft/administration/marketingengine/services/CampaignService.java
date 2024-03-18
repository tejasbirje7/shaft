package org.shaft.administration.marketingengine.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private final ObjectNode EMPTY_OBJECT_NODE;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

  @Autowired
  public CampaignService(CampaignRepository campaignRepository) {
    this.campaignRepository = campaignRepository;
    this.queryConstructor = new QueryConstructor();
    this.mapper = new ObjectMapper();
    this.EMPTY_OBJECT_NODE = mapper.createObjectNode();
    this.queryTranslator = new ShaftQueryTranslator();
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

  @Override
  public Mono<ObjectNode> qualifyUsersForCampaign(int accountId, JsonNode request) {
    CampaignCriteria cc = mapper.convertValue(request,CampaignCriteria.class);
    byte[] decodedBytesQuery = Base64.decodeBase64(cc.getQ());
    String query = new String(decodedBytesQuery, StandardCharsets.UTF_8);
    return campaignRepository.getPaginatedQueryResults(accountId,query,0)
      .map(firstResponse -> {
        log.info("First Response : {}",firstResponse);
        return ShaftResponseBuilder.buildResponse("S");
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
