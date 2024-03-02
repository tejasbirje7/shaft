package org.shaft.administration.marketingengine.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

public interface EmailDao {
  Mono<ObjectNode> sendCampaign(int accountId, ObjectNode requestObject);
}
