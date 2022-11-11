package org.shaft.administration.catalog.services;

import com.google.common.collect.Lists;
import org.shaft.administration.catalog.dao.ItemsDAO;
import org.shaft.administration.catalog.entity.item.Item;
import org.shaft.administration.catalog.repositories.items.ItemsCustomRepositoryImpl;
import org.shaft.administration.catalog.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemsDAOImpl implements ItemsDAO {

    private ItemsRepository itemsRepository;

    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public ItemsDAOImpl(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    /**
     * #TODO Implement Cache here
     * @param accountId
     * @return List
     */
    @Override
    public List<Item> getItems(int accountId, Map<String,Object> body) {
        ACCOUNT_ID.set(accountId);
        try {
            if (body!=null && body.containsKey("fields")) {
                String[] fields = ((ArrayList<String>)body.get("fields")).toArray(new String[0]);
                return itemsRepository.getItemsWithSource(fields);
            } else {
                return Lists.newArrayList(itemsRepository.findAll());
            }
        } catch (Exception ex) {
            System.out.println("Exception "+ ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Item> getBulkItems(int accountId, Map<String,Object> body) {
        ACCOUNT_ID.set(accountId);
        try {
            List<String> itemIds = (List<String>) body.get("items");
            if (body!=null && body.containsKey("fields")) {
                String[] fields = ((ArrayList<String>)body.get("fields")).toArray(new String[0]);
                return itemsRepository.getItemsWithSource(itemIds,fields);
            } else {
                return Lists.newArrayList(itemsRepository.findByIdIn(itemIds));
            }
        } catch (Exception ex) {
            System.out.println("Exception "+ex.getMessage());
            return new ArrayList<>();
        }
    }
}
