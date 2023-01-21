package org.shaft.administration.inventory.repositories;

import org.shaft.administration.inventory.entity.orders.Order;
import org.shaft.administration.inventory.repositories.order.OrdersCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrdersRepository extends ReactiveCrudRepository<Order,String>, OrdersCustomRepository {
    Flux<Order> findByI(int i);
}
