package org.shaft.administration.catalog.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ItemsDAO {

    Mono<ObjectNode> getItems(int accountId, Map<String,Object> body);
    Mono<ObjectNode> getItemsById(int accountId, Map<String,Object> body);
    Mono<ObjectNode> getBulkItems(int accountId,Map<String,Object> body);
    Mono<ObjectNode> saveItem(int accountId, Map<String,Object> body, FilePart image);
    Mono<ObjectNode> deleteItem(int accountId, Map<String, Object> body);
}
