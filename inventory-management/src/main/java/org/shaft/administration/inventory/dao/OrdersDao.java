package org.shaft.administration.inventory.dao;

import org.shaft.administration.inventory.entity.orders.Item;
import org.shaft.administration.inventory.entity.orders.Order;

import java.util.List;
import java.util.Map;

public interface OrdersDao {
    List<Object> getOrdersForI(int accountId, Map<String,Object> i);
    List<Object> getOrders(int accountId);
    boolean saveOrders(int accountId, Map<String,Object> order);
    List<Object> getBulkItemsInOrder(int accountId, Map<String,Object> itemIds);
    boolean updateOrdersStage(int accountId, Map<String,Object> status);
}
