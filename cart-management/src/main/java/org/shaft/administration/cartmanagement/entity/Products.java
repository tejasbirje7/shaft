package org.shaft.administration.cartmanagement.entity;

import java.util.Map;

public class Products {
    private Product product;
    private int quantity;
    private Map<String,String> option;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product products) {
        this.product = products;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Map<String, String> getOption() {
        return option;
    }

    public void setOption(Map<String, String> option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "Products{" +
                "product=" + product +
                ", quantity=" + quantity +
                ", option=" + option +
                '}';
    }
}
