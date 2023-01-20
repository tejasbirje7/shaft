package org.shaft.administration.inventory.controllers;

import org.shaft.administration.inventory.dao.OrdersDao;
import org.shaft.administration.inventory.entity.orders.Item;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> getOrdersForI(@RequestHeader(value="account") int account,
                                            @RequestBody Map<String,Object> i) {
        List<Object> orders = ordersDao.getOrdersForI(account, i);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S62864923",orders,headers);
    }

    @RequestMapping(value = "/orders/all", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> getOrders(@RequestHeader(value="account") int account) {
        List<Object> orders = ordersDao.getOrders(account);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S62864923",orders,headers);
    }

    @RequestMapping(value = "/orders/items",method = {RequestMethod.POST})
    public ResponseEntity<Object> getBulkItemsInOrder(@RequestHeader(value = "account") int accountId,
                                                      @RequestBody Map<String, Object> itemIds) {
        List<Object> items = ordersDao.getBulkItemsInOrder(accountId,itemIds);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S232",items,headers);
    }


    @RequestMapping(value = "/orders/placed", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> placeOrders(@RequestHeader(value="account") int accountId,
                                              @RequestBody Map<String,Object> body) {
        boolean response = ordersDao.saveOrders(accountId, body);
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(response) {
            return ShaftResponseHandler.generateResponse("Success","S7824",new ArrayList<>(),headers);
        } else {
            return ShaftResponseHandler.generateResponse("Failed","F7824",new ArrayList<>(),headers);
        }
    }

    @PostMapping(path= "/orders/stage")
    public ResponseEntity<Object> updateOrdersStage(@RequestHeader(value="account") int accountId,
                                                    @RequestBody Map<String,Object> body) {
        ordersDao.updateOrdersStage(accountId,body);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S232",new ArrayList<>(),headers);
    }
}
