package org.shaft.administration.inventory.repositories;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.shaft.administration.inventory.entity.orders.Order;
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

    public List<Order> getOrders(List<Integer> orderIds) {
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("oid",orderIds));
        NativeSearchQuery ns = new NativeSearchQueryBuilder().withQuery(query).build();
        SearchHits<Order> hits = elasticOperations.search(ns, Order.class);
        return (List<Order>)(List<?>) hits.stream().collect(Collectors.toList());
    }

}
