package org.shaft.administration.marketingengine.services;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CampaignService implements CampaignDao {
  private final CampaignRepository campaignRepository;
  public ObjectMapper mapper;
  private final QueryConstructor queryConstructor;
  private final ShaftQueryTranslator queryTranslator;

  @Autowired
  public CampaignService(CampaignRepository campaignRepository) {
    this.campaignRepository = campaignRepository;
    this.queryConstructor = new QueryConstructor();
    this.mapper = new ObjectMapper();
    this.queryTranslator = new ShaftQueryTranslator();
  }

  @Override
  public Mono<ObjectNode> checkForCampaignQualification(int accountId,Map<String,Object> request) {
    EventMetadata eventMetadata = mapper.convertValue(request.get(CampaignConstants.EVENT), EventMetadata.class);
    return campaignRepository.checkIfCampaignExistsForEvent(accountId, eventMetadata.getEid())
      .collectList()
      .mapNotNull(campaignsForEvent -> {
        if(!campaignsForEvent.isEmpty()) {
          List<Map<String,Object>> campaigns = mapper.convertValue(campaignsForEvent,List.class);
          ObjectNode queries = queryConstructor.constructMsearchQuery(campaigns,request, (Integer) request.get(CampaignConstants.IDENTITY),accountId + "_*");
          ObjectNode queryResponse = campaignRepository.checkEligibleCampaignsForI(12000,queries.get(CampaignConstants.QUERIES).asText());
          ObjectNode campaignToRender = checkCampaignToRender(queryResponse, (ArrayNode) queries.get(CampaignConstants.CID_MAP));
          if(!campaignToRender.isEmpty()) {
            return ShaftResponseBuilder.buildResponse(ShaftResponseCode.CAMPAIGNS_TO_RENDER,campaignToRender);
          }
        }
        return ShaftResponseBuilder.buildResponse(ShaftResponseCode.CAMPAIGNS_TO_RENDER);
      })
      .onErrorResume(t -> {
        log.error(CampaignLogs.CAMPAIGN_QUALIFICATION_EXCEPTION,t.getMessage(),accountId);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_CHECK_CAMPAIGN_QUALIFICATION));
      });
  }

  private ObjectNode checkCampaignToRender(ObjectNode queryResponse,ArrayNode cidMap) {
    ObjectNode campaignToRender = mapper.createObjectNode();
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

  public Mono<ObjectNode> saveCampaign(int accountId, ObjectNode requestObject) {
    ObjectNode q = (ObjectNode) requestObject.get(CampaignConstants.QUERY);
    ObjectNode te = (ObjectNode) requestObject.get(CampaignConstants.TRIGGERING_EVENT);
    ObjectNode elasticQuery = translateRawQuery(q);
    // #TODO Check if campaigns already exists for event `te`
    String encodedQuery;
    try {
      String stringQuery = mapper.writeValueAsString(elasticQuery);
      byte[] bytesEncoded = Base64.encodeBase64(stringQuery.getBytes());
      encodedQuery = new String(bytesEncoded);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    requestObject.put(CampaignConstants.STATUS,1);
    requestObject.put(CampaignConstants.QUERY,encodedQuery);
    CampaignCriteria cc = mapper.convertValue(requestObject,CampaignCriteria.class);
    return campaignRepository.save(accountId,cc)
      .flatMap(k -> Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.CAMPAIGN_SAVED)))
      .onErrorResume(t -> {
        if(isRestStatusException(t)) {
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.CAMPAIGN_SAVED));
        } else {
          log.error(CampaignLogs.FAILED_TO_SAVE_CAMPAIGN,t.getMessage(),accountId);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_SAVE_CAMPAIGN));
        }
      });
  }

  private ObjectNode translateRawQuery(ObjectNode rawQuery) {
    return queryTranslator.translateToElasticQuery(rawQuery,true);
  }

  private boolean isRestStatusException(Throwable t) {
    return t instanceof RestStatusException;
  }

}
