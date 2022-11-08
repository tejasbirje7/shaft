package org.shaft.administration.catalog.repositories;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.shaft.administration.catalog.entity.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomRepository {
    ElasticsearchOperations elasticOperations;
    QueryBuilder query;
    NativeSearchQuery ns;

    @Autowired
    public CustomRepository(ElasticsearchOperations elasticOperations) {
        this.elasticOperations = elasticOperations;
    }

    public List<Item> getItemsWithSource(List<String> itemIds, String[] fields) {
        query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("id.keyword",itemIds));
        return queryWithSource(fields);
    }

    public List<Item> getItemsWithSource(String[] fields) {
        query = QueryBuilders.matchAllQuery();
        return queryWithSource(fields);
    }

    private List<Item> queryWithSource(String[] fields) {
        //include only specific fields
        final SourceFilter filter = new FetchSourceFilter(fields, null);
        ns = new NativeSearchQueryBuilder()
                .withQuery(query)
                .withPageable(PageRequest.of(0,50))
                .withSourceFilter(filter)
                .build();
        SearchHits<Item> hits = elasticOperations.search(ns, Item.class);
        return hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

}
