package org.shaft.administration.cartmanagement.services;

import com.google.common.collect.Lists;
import org.shaft.administration.cartmanagement.dao.CartDao;
import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartDAOImpl implements CartDao {

    private CartRepository cartRepository;
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }
    @Autowired
    public CartDAOImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public boolean emptyCartItems(int accountId, int i) {
        ACCOUNT_ID.set(accountId);
        try {
            cartRepository.deleteCartBy(i);
        } catch (Exception ex) {
            return false;
        } finally {
            ACCOUNT_ID.remove();
        }
        return true;
    }

    @Override
    public List<Cart> getCartItemsForI(int accountId, int i) {
        ACCOUNT_ID.set(accountId);
        try {
            return Lists.newArrayList(cartRepository.findByI(i));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ArrayList<>();
        }
    }
}
