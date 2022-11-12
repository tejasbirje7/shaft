package org.shaft.administration.cartmanagement.repositories.custom;

import java.util.Map;

public interface CartCustomRepository {
    Long transactCartProducts(int i, Map<String,Object> product);
}
