package org.shaft.administration.inventory.repositories.order;

public interface OrdersCustomRepository {

    Long updateOrderStage(String orderId,int status);
}
