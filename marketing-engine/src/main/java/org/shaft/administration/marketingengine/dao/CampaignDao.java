package org.shaft.administration.marketingengine.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.marketingengine.entity.CampaignCriteria.CampaignCriteria;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CampaignDao {
  Mono<ObjectNode> checkIfCampaignExistsForEvent(int accountId, Map<String,Object> request);
  Mono<ObjectNode> qualifyUsersForCampaign(int accountId, JsonNode request);
  Mono<ObjectNode> saveCampaign(int accountId, ObjectNode requestObject, FilePart image);
  Mono<ObjectNode> getCampaigns(int accountId, ObjectNode requestObject);
  Mono<ObjectNode> getActivePBSCampaigns(int accountId);
}
