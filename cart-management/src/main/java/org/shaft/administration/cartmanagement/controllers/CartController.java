package org.shaft.administration.cartmanagement.controllers;

import org.shaft.administration.cartmanagement.dao.CartDao;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {
    HttpHeaders headers = new HttpHeaders();
    @Autowired
    CartDao cartDao;

    @GetMapping("/items")
    public ResponseEntity<Object> getCartItems(@RequestHeader int account,
                                               @RequestHeader int i) {
        cartDao.getCartItemsForI(account,i);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }

    @GetMapping("/remove")
    public ResponseEntity<Object> removeItemsFromCart(@RequestHeader int accountId,
                                                      @RequestHeader int i,
                                                      @RequestBody Map<String,Object> itemsToRemove) {
        // #TODO Get particular items in request body to remove from cart
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }

    @GetMapping("/empty")
    public ResponseEntity<Object> emptyCartItems(@RequestHeader(value="account") int accountId,
                                                 @RequestHeader int i) {
        cartDao.emptyCartItems(accountId,i);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }
}
