package org.shaft.administration.inventory.repositories;

import org.shaft.administration.inventory.entity.orders.Order;
import org.shaft.administration.inventory.repositories.order.OrdersCustomRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends ElasticsearchRepository<Order,String> , OrdersCustomRepository {
    public List<Order> findByI(int i);
}
