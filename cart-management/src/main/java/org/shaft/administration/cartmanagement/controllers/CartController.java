package org.shaft.administration.cartmanagement.controllers;

import org.shaft.administration.cartmanagement.dao.CartDao;
import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
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
@RequestMapping("/cart")
public class CartController {
    HttpHeaders headers = new HttpHeaders();
    CartDao cartDao;

    @Autowired
    public CartController(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @RequestMapping(value = "/get/products", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getCartProducts(@RequestHeader int account,
                                                        @RequestHeader int i) {
        return cartDao.getCartProductsForI(account,i).map(ShaftResponseHandler::generateResponse);
    }

    @RequestMapping(value = "/transact/products", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> transactCartProducts(@RequestHeader int account,
                                                             @RequestHeader int i,
                                                             @RequestBody Map<String,Object> productsToUpdate) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return cartDao.transactCartProducts(account,i,productsToUpdate).map(ShaftResponseHandler::generateResponse);
    }

    @RequestMapping(value = "/empty", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> emptyCartItems(@RequestHeader int account,
                                                       @RequestHeader int i) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        return cartDao.emptyCartItems(account,i).map(ShaftResponseHandler::generateResponse);
    }



//    @RequestMapping(value = "/save/products", method = { RequestMethod.GET, RequestMethod.POST })
//    public Mono<ResponseEntity<Object>> saveCartProducts(@RequestHeader int account,
//                                                   @RequestBody Map<String,Object> cart) {
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        return cartDao.saveCartItems(account,cart)
//          .map(resource -> ShaftResponseHandler.generateResponse("Success","S12345",resource,headers));
//    }
}
