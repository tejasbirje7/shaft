package org.shaft.administration.catalog.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ItemsDAO {

    Mono<ObjectNode> getItems(int accountId, Map<String,Object> body);
    Mono<ObjectNode> getItemsById(int accountId, Map<String,Object> body);
    Mono<ObjectNode> getBulkItems(int accountId,Map<String,Object> body);
    Mono<ObjectNode> saveItem(int accountId, Map<String,Object> body);
}
