package org.shaft.administration.cartmanagement.repositories.custom;

import org.shaft.administration.cartmanagement.entity.Products;

import java.util.List;

public interface CartCustomRepository {
    Long updateCartProducts(int i, List<Products> products);
}
