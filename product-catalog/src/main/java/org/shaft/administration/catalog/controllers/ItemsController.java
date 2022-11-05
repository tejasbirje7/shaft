package org.shaft.administration.catalog.controllers;

import org.shaft.administration.catalog.common.ShaftResponseHandler;
import org.shaft.administration.catalog.dao.ItemsDAO;
import org.shaft.administration.catalog.entity.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class ItemsController {
    private ItemsDAO itemsDao;
    @Autowired
    public void setItemsDao(ItemsDAO itemsDao) {
        this.itemsDao = itemsDao;
    }

    /**
     * #TODO Check best practices for threadlocal to set in controller or service layer
     * @param account
     * @return
     */
    @PostMapping("/items")
    public ResponseEntity<Object> handleShopRequest(@RequestHeader(value="account") int account) {
        List<Item> items = itemsDao.getItems(account);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",items,headers);
    }
}
