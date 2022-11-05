package org.shaft.administration.inventorymanagement.repositories;

import com.google.common.collect.Lists;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.shaft.administration.inventorymanagement.entity.orders.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomRepository {

    @Autowired
    ElasticsearchOperations elasticOperations;

    public List<Order> getOrders(List<Integer> orderIds) {
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("oid",orderIds));
        NativeSearchQuery ns = new NativeSearchQueryBuilder().withQuery(query).build();
        SearchHits<Order> hits = elasticOperations.search(ns, Order.class);
        return (List<Order>)(List<?>) hits.stream().collect(Collectors.toList());
    }

}
