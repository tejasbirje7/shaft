package org.shaft.administration.cartmanagement.dao;

import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.entity.Products;

import java.util.List;
import java.util.Map;

public interface CartDao {
    public boolean emptyCartItems(int accountId,int i);
    public List<Cart> getCartProductsForI(int accountId, int i);
    public Map<String,Object> transactCartProducts(int accountId, int i, Map<String,Object> product);
    public boolean saveCartItems(int account, Map<String,Object> cart);
}
