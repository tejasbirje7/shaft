package org.shaft.administration.catalog.services;

import com.google.common.collect.Lists;
import org.shaft.administration.catalog.dao.ItemsDAO;
import org.shaft.administration.catalog.entity.item.Item;
import org.shaft.administration.catalog.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public List<Item> getItems(int accountId) {
        ACCOUNT_ID.set(accountId);
        try {
            return Lists.newArrayList(itemsRepository.findAll());
        } catch (Exception ex) {
            System.out.println("Exception "+ ex.getMessage());
            return new ArrayList<>();
        }
    }
}
