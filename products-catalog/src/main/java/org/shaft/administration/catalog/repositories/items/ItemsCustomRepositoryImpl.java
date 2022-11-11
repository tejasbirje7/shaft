package org.shaft.administration.catalog.repositories.items;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.shaft.administration.catalog.entity.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemsCustomRepositoryImpl implements ItemsCustomRepository{
    ElasticsearchOperations elasticOperations;
    QueryBuilder query;
    NativeSearchQuery ns;

    @Autowired
    public ItemsCustomRepositoryImpl(ElasticsearchOperations elasticOperations) {
        this.elasticOperations = elasticOperations;
    }

    @Override
    public List<Item> getItemsWithSource(List<String> itemIds, String[] fields) {
        query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("id.keyword",itemIds));
        return queryWithSource(fields);
    }

    @Override
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
