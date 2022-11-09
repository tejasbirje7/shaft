package org.shaft.administration.cartmanagement.dao;

import org.shaft.administration.cartmanagement.entity.Cart;

import java.util.List;

public interface CartDao {
    public boolean emptyCartItems(int accountId,int i);
    public List<Cart> getCartItemsForI(int accountId, int i);
}
