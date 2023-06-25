package org.shaft.administration.catalog.repositories.items;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.shaft.administration.catalog.entity.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class ItemsCustomRepositoryImpl implements ItemsCustomRepository{
    private QueryBuilder query;
    private NativeSearchQuery ns;
    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;

    @Autowired
    public ItemsCustomRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations) {
        this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
    }

    @Override
    public Flux<Item> getItemsWithSource(List<String> itemIds, String[] fields) {
        query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("id.keyword",itemIds));
        return queryWithSource(fields);
    }

    @Override
    public Flux<Item> getItemsWithSource(String[] fields) {
        query = QueryBuilders.matchAllQuery();
        return queryWithSource(fields);
    }

    @Override
    public Mono<Item> getItemById(String itemId) {
        query = QueryBuilders.boolQuery()
          .must(QueryBuilders.termQuery("id.keyword",itemId));
        ns = new NativeSearchQueryBuilder()
          .withQuery(query)
          .withMaxResults(1)
          .build();
        return reactiveElasticsearchOperations.search(ns, Item.class)
          .map(SearchHit::getContent)
          .next()
          .filter(Objects::nonNull)
          .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }

    private Script prepareProductsUpdateScript(Map<String,Object> itemDetails) {
        String scriptStr = "ctx._source.products = params.get(\"product\")";
        return new Script(ScriptType.INLINE, "painless", scriptStr, itemDetails);
    }

    private Flux<Item> queryWithSource(String[] fields) {
        // include only specific fields
        final SourceFilter filter = new FetchSourceFilter(fields, null);
        ns = new NativeSearchQueryBuilder()
                .withQuery(query)
                .withMaxResults(100)
                .withPageable(PageRequest.of(0,50))
                .withSourceFilter(filter)
                .build();
       return reactiveElasticsearchOperations.search(ns, Item.class)
               .map(SearchHit::getContent)
               .filter(Objects::nonNull)
               .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
        //return hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

}
