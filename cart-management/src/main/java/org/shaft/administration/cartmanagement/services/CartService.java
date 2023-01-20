package org.shaft.administration.cartmanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.cartmanagement.dao.CartDao;
import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CartService implements CartDao {

  private final CartRepository cartRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }
  @Autowired
  public CartService(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  @Override
  public Mono<Boolean> emptyCartItems(int accountId, int i) {
    ACCOUNT_ID.set(accountId);
    try {
      return cartRepository.deleteUsersCart(i)
        .map(totalDeleted -> Mono.just(totalDeleted > 0))
        .hasElement();
    } finally {
      ACCOUNT_ID.remove();
    }
  }

  @Override
  public Mono<List<Cart>> getCartProductsForI(int accountId, int i) {
    ACCOUNT_ID.set(accountId);
    try {
      // #TODO Check response and apply flatMap if required
      return cartRepository.findByI(i).collectList();
    } finally {
      ACCOUNT_ID.remove();
    }
  }

  @Override
  public Mono<Map<String, Object>> transactCartProducts(int accountId, int i, Map<String,Object> products) {
    ACCOUNT_ID.set(accountId);
    // #TODO Check here if already there's cart for the `i` and call saveCartItems if cart doesn't exist.
    try {
      // #TODO check if we can remove dependency of count API here by checking size of products in request. If 1 then save & more then 1 is update
      return cartRepository.countByI(i)
        .publishOn(Schedulers.boundedElastic())
        .map( count -> {
            ACCOUNT_ID.set(accountId);
            Map<String,Object> response = new HashMap<>();
            if(count > 0) {
              cartRepository.transactCartProducts(i,products)
                .map(totalUpdated -> {
                  if ( totalUpdated > 0) {
                    response.put("count",totalUpdated);
                  } else {
                    response.put("count",0);
                  }
                  response.put("action","updated");
                  return response;
                }).block();
            } else {
              products.put("i",i);
              Cart c = objectMapper.convertValue(products, Cart.class);
              ACCOUNT_ID.set(accountId);
              cartRepository.save(c)
                .map(resp -> {
                  log.info("Saved successfully {}",resp);
                  response.put("count" , 1);
                  response.put("mode","added");
                  return response;
                })
                .doOnError(t -> {
                  if(t instanceof NoSuchIndexException) {
                    log.error("Exception - {} , No such index {}",t.getMessage(),ACCOUNT_ID.get());
                  }
                  if (t instanceof RestStatusException) {
                    // #TODO Check this exception which appears always even if document gets saved properly
                    log.error("RestStatusException {}",t.getMessage(),t);
                  }
                  response.put("count",0);
                }).block();
            }
            return response;
          }
        );
    } finally {
      ACCOUNT_ID.remove();
    }
  }
}
