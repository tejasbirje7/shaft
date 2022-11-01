package org.shaft.administration.productcatalog.dao;

import org.shaft.administration.productcatalog.entity.Item;

import java.util.List;

public interface ItemsDao {

    public List<Item> getItems(int accountId);
}
