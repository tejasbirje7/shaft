package org.shaft.administration.inventory.controllers;

import org.shaft.administration.inventory.dao.OrdersDao;
import org.shaft.administration.inventory.entity.orders.Item;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class OrdersController {
    OrdersDao ordersDao;
    @Autowired
    public OrdersController(OrdersDao ordersDao) {
        this.ordersDao = ordersDao;
    }

    @RequestMapping(value = "/orders", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getOrdersForI(@RequestHeader(value="account") int account,
                                                      @RequestBody Map<String,Object> i) {
        return ordersDao.getOrdersForI(account,i).map(ShaftResponseHandler::generateResponse);
    }

    @RequestMapping(value = "/orders/all", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getOrders(@RequestHeader(value="account") int account) {
        return ordersDao.getOrders(account).map(ShaftResponseHandler::generateResponse);
    }

    @RequestMapping(value = "/orders/items",method = {RequestMethod.POST})
    public Mono<ResponseEntity<Object>> getBulkItemsInOrder(@RequestHeader(value = "account") int accountId,
                                                            @RequestBody Map<String, Object> itemIds) {
        return ordersDao.getBulkItemsInOrder(accountId,itemIds).map(ShaftResponseHandler::generateResponse);
    }


    @RequestMapping(value = "/orders/placed", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> placeOrders(@RequestHeader(value="account") int accountId,
                                                    @RequestBody Map<String,Object> body) {
        return ordersDao.saveOrders(accountId, body).map(ShaftResponseHandler::generateResponse);
    }

    @PostMapping(path= "/orders/stage")
    public Mono<ResponseEntity<Object>> updateOrdersStage(@RequestHeader(value="account") int accountId,
                                                          @RequestBody Map<String,Object> body) {
        return ordersDao.updateOrdersStage(accountId,body).map(ShaftResponseHandler::generateResponse);
    }
}
