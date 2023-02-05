package org.shaft.administration.inventory.repositories.order;

import reactor.core.publisher.Mono;

public interface OrdersCustomRepository {
    Mono<Long> updateOrderStage(int orderId, int status, int accountId);
}
