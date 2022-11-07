package org.shaft.administration.catalog.dao;

import org.shaft.administration.catalog.entity.item.Item;

import java.util.List;
import java.util.Map;

public interface ItemsDAO {

    public List<Item> getItems(int accountId);
    public List<Item> getBulkItems(int accountId,Map<String,Object> body);
}
