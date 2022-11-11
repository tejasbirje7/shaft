package org.shaft.administration.cartmanagement.repositories.custom;

import java.util.Map;

public interface CartCustomRepository {
    Long addProductToCart(int i, Map<String,Object> product);
}
