package org.shaft.administration.cartmanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.cartmanagement.entity.Cart;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface CartDao {
    Mono<ObjectNode> emptyCartItems(int accountId, int i);
    Mono<ObjectNode> getCartProductsForI(int accountId, int i);
    Mono<ObjectNode> transactCartProducts(int accountId, int i, Map<String, Object> product);

}
