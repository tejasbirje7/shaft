package org.shaft.administration.marketingengine.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface EmailDao {
  Mono<ObjectNode> sendCampaign(int accountId, ObjectNode requestObject);
}
