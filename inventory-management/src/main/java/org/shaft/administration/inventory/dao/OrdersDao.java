package org.shaft.administration.inventory.dao;

import java.util.List;

public interface OrdersDao {
    public List<Object> getOrdersForI(int accountId, int i);
}
