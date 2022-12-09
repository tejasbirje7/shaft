package org.shaft.administration.inventory.repositories.order;

public interface OrdersCustomRepository {

    Long updateOrderStage(int orderId,int status);
}
