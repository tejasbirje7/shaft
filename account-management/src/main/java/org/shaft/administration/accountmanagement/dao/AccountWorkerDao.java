package org.shaft.administration.accountmanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AccountWorkerDao {
  Mono<ObjectNode> bootstrapAccount(Map<String,Object> request);
}
