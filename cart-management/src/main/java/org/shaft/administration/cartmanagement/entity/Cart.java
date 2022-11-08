package org.shaft.administration.cartmanagement.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.UUID;

@Document(indexName = "1600_cart")
public class Cart {
    @Id
    private String _id = UUID.randomUUID().toString();
    private int i;
    private List<ProductsInCart> productsInCarts;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public List<ProductsInCart> getProducts() {
        return productsInCarts;
    }

    public void setProducts(List<ProductsInCart> productsInCarts) {
        this.productsInCarts = productsInCarts;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "i=" + i +
                ", productsInCarts=" + productsInCarts +
                '}';
    }
}
