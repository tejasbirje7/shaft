package org.shaft.administration.productcatalog.controllers;

import org.shaft.administration.productcatalog.common.ShaftResponseHandler;
import org.shaft.administration.productcatalog.dao.ItemsDao;
import org.shaft.administration.productcatalog.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ItemsController {

    @Autowired
    private ItemsDao itemsDao;

    @GetMapping("/items")
    public ShaftResponseHandler handleShopRequest(@RequestHeader(value="account") int account) {

        List<Item> items = itemsDao.getItems(account);
        ShaftResponseHandler response = new ShaftResponseHandler();
        Map<String,Object> m = new HashMap<>();
        m.put("data",items);
        response.setResult(m);
        return response;
    }
}
