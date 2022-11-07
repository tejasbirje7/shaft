package org.shaft.administration.inventorymanagement.dao;

import org.shaft.administration.inventorymanagement.entity.orders.Order;

import java.util.List;

public interface OrdersDao {
    public List<Object> getOrdersForI(int accountId, int i);
}
