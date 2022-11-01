package org.shaft.administration.productcatalog.services;

import com.google.common.collect.Lists;
import org.shaft.administration.productcatalog.dao.ItemsDao;
import org.shaft.administration.productcatalog.entity.Item;
import org.shaft.administration.productcatalog.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemsDaoImpl implements ItemsDao {

    private ItemsRepository itemsRepository;

    public static int ACCOUNT_ID;

    @Autowired
    public ItemsDaoImpl(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public List<Item> getItems(int accountId) {
        ACCOUNT_ID = accountId;
        List<Item> items = new ArrayList<>();
        try {
            items = Lists.newArrayList(itemsRepository.findAll());
        } catch (Exception ex) {
            System.out.println("Exception "+ ex.getMessage());
        }
        return items;
    }

    public static void main(String[] args) {
    }

    public static int getAccount() {
        return ACCOUNT_ID;
    }
}
