package org.shaft.administration.inventory.dao;

import org.shaft.administration.inventory.entity.orders.Order;

import java.util.List;
import java.util.Map;

public interface OrdersDao {
    public List<Object> getOrdersForI(int accountId, Map<String,Object> i);
    public boolean saveOrders(int accountId, Map<String,Object> order);
}
