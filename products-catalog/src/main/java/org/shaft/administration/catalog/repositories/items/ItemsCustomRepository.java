package org.shaft.administration.catalog.repositories.items;

import org.elasticsearch.index.query.QueryBuilders;
import org.shaft.administration.catalog.entity.item.Item;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public interface ItemsCustomRepository {
    Flux<Item> getItemsWithSource(List<String> itemIds, String[] fields);
    Flux<Item> getItemsWithSource(String[] fields);
    Mono<Item> getItemById(String itemId);
}
