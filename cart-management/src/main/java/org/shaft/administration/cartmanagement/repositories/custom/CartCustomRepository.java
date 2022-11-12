package org.shaft.administration.cartmanagement.repositories.custom;

import org.shaft.administration.cartmanagement.entity.Product;
import org.shaft.administration.cartmanagement.entity.Products;

import java.util.List;
import java.util.Map;

public interface CartCustomRepository {
    Long transactCartProducts(int i, Map<String,Object> product);
}
