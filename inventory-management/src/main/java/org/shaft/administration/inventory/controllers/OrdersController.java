package org.shaft.administration.inventory.controllers;

import org.shaft.administration.inventory.common.ShaftResponseHandler;
import org.shaft.administration.inventory.dao.OrdersDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class OrdersController {

    @Autowired
    OrdersDao ordersDao;

    @GetMapping("/orders")
    public ResponseEntity<Object> getOrders(@RequestHeader(value="account") int account,
                                                    @RequestHeader(value="i") int i) {
        List<Object> orders = ordersDao.getOrdersForI(account, i);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S62864923",orders,headers);
    }
}
