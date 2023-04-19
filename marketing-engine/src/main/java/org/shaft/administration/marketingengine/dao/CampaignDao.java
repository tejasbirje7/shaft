package org.shaft.administration.marketingengine.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CampaignDao {
  Mono<ObjectNode> checkForCampaignQualification(int accountId, Map<String,Object> request);
  Mono<ObjectNode> saveCampaign(int accountId, ObjectNode requestObject);
}
