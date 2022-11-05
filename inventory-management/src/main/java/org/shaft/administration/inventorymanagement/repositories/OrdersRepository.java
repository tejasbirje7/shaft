package org.shaft.administration.inventorymanagement.repositories;

import org.shaft.administration.inventorymanagement.entity.orders.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends ElasticsearchRepository<Order,String> {
    public List<Order> findByI(int i);
}
