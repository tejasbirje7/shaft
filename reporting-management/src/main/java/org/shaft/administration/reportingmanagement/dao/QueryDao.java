package org.shaft.administration.reportingmanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

import java.util.Map;
public interface QueryDao {
    Mono<ObjectNode> getQueryResults(int accountId, Map<String,Object> rawQuery);
    Mono<ObjectNode> evaluateEncodedQueries(int accountId,Map<String,Object> request);
}
