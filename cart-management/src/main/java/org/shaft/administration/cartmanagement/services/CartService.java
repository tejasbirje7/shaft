package org.shaft.administration.cartmanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.cartmanagement.dao.CartDao;
import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CartService implements CartDao {

    private final CartRepository cartRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }
    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Mono<Boolean> emptyCartItems(int accountId, int i) {
        ACCOUNT_ID.set(accountId);
        try {
            return cartRepository.deleteUsersCart(i)
              .map(totalDeleted -> Mono.just(totalDeleted > 0))
              .hasElement();
        } finally {
            ACCOUNT_ID.remove();
        }
    }

    @Override
    public Mono<List<Cart>> getCartProductsForI(int accountId, int i) {
        ACCOUNT_ID.set(accountId);
        try {
            // #TODO Check response and apply flatMap if required
            return cartRepository.findByI(i).collectList();
        } finally {
            ACCOUNT_ID.remove();
        }
    }

    @Override
    public Mono<Map<String, Object>> transactCartProducts(int accountId, int i, Map<String,Object> product) {
        ACCOUNT_ID.set(accountId);
        // #TODO Check here if already there's cart for the `i` and call saveCartItems if cart doesn't exist.
        try {
            return cartRepository.transactCartProducts(i,product)
              .map(totalUpdated -> {
                  Map<String,Object> response = new HashMap<>();
                  if ( totalUpdated > 0) {
                      response.put("updated",totalUpdated);
                  } else {
                      response.put("updated",0);
                  }
                  return response;
              }); // #TODO Handle Errors here with reactive call doOnError
        } finally {
            ACCOUNT_ID.remove();
        }
    }

    @Override
    public boolean saveCartItems(int account, Map<String,Object> cart) {
        ACCOUNT_ID.set(account);
        Cart c = objectMapper.convertValue(cart,Cart.class);
        try {
            cartRepository.save(c);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

}
