package org.shaft.administration.catalog.repositories;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.shaft.administration.catalog.entity.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomRepository {
    ElasticsearchOperations elasticOperations;

    @Autowired
    public CustomRepository(ElasticsearchOperations elasticOperations) {
        this.elasticOperations = elasticOperations;
    }


    public List<Item> getItems(List<String> itemIds) {
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("id.keyword",itemIds));
        NativeSearchQuery ns = new NativeSearchQueryBuilder().withQuery(query).build();
        SearchHits<Item> hits = elasticOperations.search(ns, Item.class);
        return (List<Item>)(List<?>) hits.stream().collect(Collectors.toList());
    }

}
