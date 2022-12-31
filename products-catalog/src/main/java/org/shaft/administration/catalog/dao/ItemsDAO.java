package org.shaft.administration.catalog.dao;

import org.shaft.administration.catalog.entity.item.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface ItemsDAO {

    Flux<Item> getItems(int accountId,Map<String,Object> body);
    Flux<Item> getBulkItems(int accountId,Map<String,Object> body);
    Mono<Item> saveItem(int accountId, Map<String,Object> body);
}
