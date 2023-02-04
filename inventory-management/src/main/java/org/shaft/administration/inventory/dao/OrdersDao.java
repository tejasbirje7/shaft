package org.shaft.administration.inventory.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.inventory.entity.orders.Item;
import org.shaft.administration.inventory.entity.orders.Order;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface OrdersDao {
    Mono<ObjectNode> getOrdersForI(int accountId, Map<String,Object> i);
    Mono<ObjectNode> getOrders(int accountId);
    Mono<ObjectNode> saveOrders(int accountId, Map<String,Object> order);
    Mono<ObjectNode>getBulkItemsInOrder(int accountId, Map<String,Object> itemIds);
    Mono<ObjectNode> updateOrdersStage(int accountId, Map<String,Object> status);
}
