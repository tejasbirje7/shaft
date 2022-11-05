package org.shaft.administration.inventorymanagement.controllers;

import org.shaft.administration.inventorymanagement.common.ShaftResponseHandler;
import org.shaft.administration.inventorymanagement.dao.OrdersDao;
import org.shaft.administration.inventorymanagement.entity.orders.Order;
import org.shaft.administration.inventorymanagement.repositories.OrdersRepository;
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
@RequestMapping("/inventory")
public class OrdersController {

    @Autowired
    OrdersDao ordersDao;

    @PostMapping("/orders")
    public ResponseEntity<Object> handleShopRequest(@RequestHeader(value="account") int account,
                                                    @RequestHeader(value="i") int i) {
        List<Order> orders = ordersDao.getOrdersForI(account, i);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",orders,headers);
    }
}
