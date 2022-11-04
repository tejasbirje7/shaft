package org.shaft.administration.catalog.dao;

import org.shaft.administration.catalog.entity.Item;

import java.util.List;

public interface ItemsDao {

    public List<Item> getItems(int accountId);
}
