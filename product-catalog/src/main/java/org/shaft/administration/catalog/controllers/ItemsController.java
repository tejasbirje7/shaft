package org.shaft.administration.catalog.controllers;

import org.shaft.administration.catalog.common.ShaftResponseHandler;
import org.shaft.administration.catalog.dao.ItemsDao;
import org.shaft.administration.catalog.entity.Item;
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

    @Autowired
    private ItemsDao itemsDao;

    @PostMapping("/items")
    public ResponseEntity<Object> handleShopRequest(@RequestHeader(value="account") int account) {
        List<Item> items = itemsDao.getItems(account);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",items,headers);
    }
}
