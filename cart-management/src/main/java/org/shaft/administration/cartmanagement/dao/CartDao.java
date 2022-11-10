package org.shaft.administration.cartmanagement.dao;

import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.entity.Products;

import java.util.List;
import java.util.Map;

public interface CartDao {
    public boolean emptyCartItems(int accountId,int i);
    public List<Cart> getCartItemsForI(int accountId, int i);
    public Map<String,Object> updateCartProducts(int accountId, int i, List<Products> products);
}
