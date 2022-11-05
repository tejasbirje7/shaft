package org.shaft.administration.catalog.dao;

import org.shaft.administration.catalog.entity.item.Item;

import java.util.List;

public interface ItemsDAO {

    public List<Item> getItems(int accountId);
}
