package org.shaft.administration.cartmanagement.controllers;

import org.shaft.administration.cartmanagement.common.ShaftResponseHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/cart")
public class CartController {
    HttpHeaders headers = new HttpHeaders();

    @GetMapping("/remove/items")
    public ResponseEntity<Object> removeItemsFromCart(@RequestHeader int accountId,
                                                      @RequestHeader int i) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }

    @GetMapping("/empty")
    public ResponseEntity<Object> emptyCartItems(@RequestHeader int accountId,
                                                      @RequestHeader int i) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }
}
