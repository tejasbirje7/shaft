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
    HttpHeaders headers = new HttpHeaders();
    @Autowired
    public OrdersController(OrdersDao ordersDao) {
        this.ordersDao = ordersDao;
    }

    @RequestMapping(value = "/orders", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getOrdersForI(@RequestHeader(value="account") int account,
                                                      @RequestBody Map<String,Object> i) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ordersDao.getOrdersForI(account,i)
          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
    }

    @RequestMapping(value = "/orders/all", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getOrders(@RequestHeader(value="account") int account) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ordersDao.getOrders(account)
          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
    }

    @RequestMapping(value = "/orders/items",method = {RequestMethod.POST})
    public Mono<ResponseEntity<Object>> getBulkItemsInOrder(@RequestHeader(value = "account") int accountId,
                                                            @RequestBody Map<String, Object> itemIds) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ordersDao.getBulkItemsInOrder(accountId,itemIds)
          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
    }


    @RequestMapping(value = "/orders/placed", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> placeOrders(@RequestHeader(value="account") int accountId,
                                                    @RequestBody Map<String,Object> body) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ordersDao.saveOrders(accountId, body)
          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
    }

    @PostMapping(path= "/orders/stage")
    public Mono<ResponseEntity<Object>> updateOrdersStage(@RequestHeader(value="account") int accountId,
                                                          @RequestBody Map<String,Object> body) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ordersDao.updateOrdersStage(accountId,body)
          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
    }
}
