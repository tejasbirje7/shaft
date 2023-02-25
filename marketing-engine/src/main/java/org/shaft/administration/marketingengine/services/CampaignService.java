package org.shaft.administration.marketingengine.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.marketingengine.dao.CampaignDao;
import org.shaft.administration.marketingengine.entity.EventRequest.EventMetadata;
import org.shaft.administration.marketingengine.repositories.CampaignRepository;
import org.shaft.administration.obligatory.campaigns.service.QueryConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CampaignService implements CampaignDao {
  private CampaignRepository campaignRepository;
  public ObjectMapper mapper = new ObjectMapper();
  private final QueryConstructor queryConstructor;

  @Autowired
  public CampaignService(CampaignRepository campaignRepository) {
    this.campaignRepository = campaignRepository;
    this.queryConstructor = new QueryConstructor();
    this.mapper = new ObjectMapper();
  }

  @Override
  public void checkForCampaignQualification(int accountId,Map<String,Object> request) {
    EventMetadata eventMetadata = mapper.convertValue(request.get("e"), EventMetadata.class);
    campaignRepository.checkIfCampaignExistsForEvent(accountId, eventMetadata.getEid())
      .collectList()
      .mapNotNull(campaignsForEvent -> {
        if(!campaignsForEvent.isEmpty()) {
          List<ObjectNode> campaigns = mapper.convertValue(campaignsForEvent,List.class);
          ObjectNode queries = queryConstructor.constructMsearchQuery(campaigns,request,166739893);
          ObjectNode queryResponse = campaignRepository.checkEligibleCampaignsForI(12000,queries.get("queries").asText());
          ObjectNode campaignToRender = checkCampaignToRender(queryResponse, (ArrayNode) queries.get("cidMap"));
          if(!campaignToRender.isEmpty()) {
            return campaignToRender;
          }
        }
        return null;
      })
      .block();
  }

  private ObjectNode checkCampaignToRender(ObjectNode queryResponse,ArrayNode cidMap) {
    ObjectNode campaignToRender = mapper.createObjectNode();
    int t = 0;
    for (int i = 0; i < cidMap.size(); i++) {
      if(cidMap.has("cid") && cidMap.get("cid").asInt() > t) {
        if(queryResponse.has("responses") && queryResponse.get("responses").size() > i) {
          if(!queryResponse.get("responses").get(i).get("hits").get("hits").isEmpty()) {
            t = cidMap.get(i).get("cid").asInt();
          }
        } else {
          campaignToRender = (ObjectNode) cidMap.get(i);
          t = cidMap.get(i).get("cid").asInt();
        }
      }
    }
    return campaignToRender;
  }

}
