package org.shaft.administration.cartmanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.shaft.administration.cartmanagement.dao.CartDao;
import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.entity.Product;
import org.shaft.administration.cartmanagement.entity.Products;
import org.shaft.administration.cartmanagement.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartDAOImpl implements CartDao {

    private CartRepository cartRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
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
    public List<Cart> getCartProductsForI(int accountId, int i) {
        ACCOUNT_ID.set(accountId);
        try {
            return Lists.newArrayList(cartRepository.findByI(i));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ArrayList<>();
        } finally {
            ACCOUNT_ID.remove();
        }
    }

    @Override
    public Map<String, Object> transactCartProducts(int accountId, int i, Map<String,Object> product) {
        ACCOUNT_ID.set(accountId);
        // #TODO Check here if already there's cart for the `i` and call saveCartItems if cart doesn't exist.
        Map<String,Object> response = new HashMap<>();
        try {
            Long updated = cartRepository.transactCartProducts(i,product);
            response.put("updated",updated);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            response.put("updated",0);
        } finally {
            ACCOUNT_ID.remove();
        }
        return response;
    }

    @Override
    public boolean saveCartItems(int account, Map<String,Object> cart) {
        ACCOUNT_ID.set(account);
        Cart c = objectMapper.convertValue(cart,Cart.class);
        try {
            cartRepository.save(c);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

}
