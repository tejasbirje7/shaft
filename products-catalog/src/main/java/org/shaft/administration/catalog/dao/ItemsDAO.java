package org.shaft.administration.catalog.dao;

import org.shaft.administration.catalog.entity.item.Item;

import java.util.List;
import java.util.Map;

public interface ItemsDAO {

    List<Item> getItems(int accountId,Map<String,Object> body);
    List<Item> getBulkItems(int accountId,Map<String,Object> body);
    Item saveItem(int accountId,Map<String,Object> body);
}
