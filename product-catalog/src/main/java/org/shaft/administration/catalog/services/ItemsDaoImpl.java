package org.shaft.administration.catalog.services;

import com.google.common.collect.Lists;
import org.shaft.administration.catalog.dao.ItemsDao;
import org.shaft.administration.catalog.entity.Item;
import org.shaft.administration.catalog.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemsDaoImpl implements ItemsDao {

    private ItemsRepository itemsRepository;

    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public ItemsDaoImpl(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

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
