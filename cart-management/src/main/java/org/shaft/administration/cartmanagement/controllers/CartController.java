package org.shaft.administration.cartmanagement.controllers;

import org.shaft.administration.cartmanagement.dao.CartDao;
import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.entity.Product;
import org.shaft.administration.cartmanagement.entity.Products;
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
@RequestMapping("/cart")
public class CartController {
    HttpHeaders headers = new HttpHeaders();
    @Autowired
    CartDao cartDao;

    @RequestMapping(value = "/get/products", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> getCartProducts(@RequestHeader int account,
                                                  @RequestHeader int i) {
        List<Cart> response = cartDao.getCartProductsForI(account,i);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",response, headers);
    }

    @RequestMapping(value = "/transact/products", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> transactCartProducts(@RequestHeader int account,
                                                      @RequestHeader int i,
                                                      @RequestBody Map<String,Object> productsToUpdate) {
        cartDao.transactCartProducts(account,i,productsToUpdate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }

    @RequestMapping(value = "/save/products", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> saveCartProducts(@RequestHeader int account,
                                                   @RequestBody Map<String,Object> cart) {
        cartDao.saveCartItems(account,cart);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }

    @RequestMapping(value = "/empty", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> emptyCartItems(@RequestHeader int account,
                                                 @RequestHeader int i) {
        cartDao.emptyCartItems(account,i);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S7394",new ArrayList<>(), headers);
    }
}
