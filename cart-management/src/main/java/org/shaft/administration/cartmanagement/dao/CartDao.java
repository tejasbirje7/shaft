package org.shaft.administration.cartmanagement.dao;

import org.shaft.administration.cartmanagement.entity.Cart;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface CartDao {
    Mono<Boolean> emptyCartItems(int accountId, int i);

    Mono<List<Cart>> getCartProductsForI(int accountId, int i);

    Mono<Map<String, Object>> transactCartProducts(int accountId, int i, Map<String, Object> product);

}
