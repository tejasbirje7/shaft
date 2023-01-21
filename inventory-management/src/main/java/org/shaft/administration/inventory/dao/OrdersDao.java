package org.shaft.administration.inventory.dao;

import org.shaft.administration.inventory.entity.orders.Item;
import org.shaft.administration.inventory.entity.orders.Order;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface OrdersDao {
    Mono<List<Object>> getOrdersForI(int accountId, Map<String,Object> i);
    Mono<List<Order>> getOrders(int accountId);
    Mono<Order> saveOrders(int accountId, Map<String,Object> order);
    Mono<List<Item>> getBulkItemsInOrder(int accountId, Map<String,Object> itemIds);
    Mono<Map<String,Long>> updateOrdersStage(int accountId, Map<String,Object> status);
}
