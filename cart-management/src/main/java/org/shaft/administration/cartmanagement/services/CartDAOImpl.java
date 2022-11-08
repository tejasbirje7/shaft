package org.shaft.administration.cartmanagement.services;

import org.shaft.administration.cartmanagement.dao.CartDao;
import org.shaft.administration.cartmanagement.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            boolean response = cartRepository.deleteByI(i);
            if(response) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        } finally {
            ACCOUNT_ID.remove();
        }
    }
}
