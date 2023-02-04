package org.shaft.administration.cartmanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.cartmanagement.constants.CartConstants;
import org.shaft.administration.cartmanagement.constants.CartLogs;
import org.shaft.administration.cartmanagement.dao.CartDao;
import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.repositories.CartRepository;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
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
  private final ObjectMapper mapper = new ObjectMapper();
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }
  @Autowired
  public CartService(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  @Override
  public Mono<ObjectNode> emptyCartItems(int accountId, int i) {
    ACCOUNT_ID.set(accountId);
    ObjectNode responseNode = mapper.createObjectNode();
    return cartRepository.deleteUsersCart(i)
      .map(totalDeleted -> {
        ACCOUNT_ID.remove();
        if(totalDeleted > 0) {
          return ShaftResponseBuilder.buildResponse(ShaftResponseCode.REMOVED_CART_ITEMS,
            responseNode.put(CartConstants.DELETED,true));
        } else {
          return ShaftResponseBuilder.buildResponse(ShaftResponseCode.REMOVED_CART_ITEMS,
            responseNode.put(CartConstants.DELETED,false));
        }
      })
      .onErrorResume(error -> {
        ACCOUNT_ID.remove();
        log.error(CartLogs.EMPTY_CART_EXCEPTION,error,accountId,i);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_FAILED_TO_EMPTY_CART,
          responseNode.put(CartConstants.DELETED,false)));
      });
  }

  @Override
  public Mono<ObjectNode> getCartProductsForI(int accountId, int i) {
    ACCOUNT_ID.set(accountId);
    return cartRepository.findByI(i)
      .collectList()
      .map(products -> {
        ACCOUNT_ID.remove();
        return ShaftResponseBuilder.buildResponse(ShaftResponseCode.FETCHED_CART_ITEMS,
          mapper.valueToTree(products));
      })
      .onErrorResume(error -> {
        ACCOUNT_ID.remove();
        log.error(CartLogs.UNABLE_FETCHING_CART_PRODUCTS,error,accountId,i);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_FETCH_CART_FOR_I));
      });
  }

  @Override
  public Mono<ObjectNode> transactCartProducts(int accountId, int i, Map<String,Object> products) {
    ACCOUNT_ID.set(accountId);
    // #TODO Check here if already there's cart for the `i` and call saveCartItems if cart doesn't exist.
    // #TODO check if we can remove dependency of count API here by checking size of products in request. If 1 then save & more then 1 is update
    return cartRepository.countByI(i)
      .publishOn(Schedulers.boundedElastic())
      .mapNotNull(count -> {
          ACCOUNT_ID.set(accountId);
          ObjectNode response = mapper.createObjectNode();
          if(count > 0) {
            return cartRepository.transactCartProducts(i,products)
              .map(totalUpdated -> {
                ACCOUNT_ID.remove();
                if ( totalUpdated > 0) {
                  response.put(CartConstants.COUNT,totalUpdated);
                } else {
                  response.put(CartConstants.COUNT,0);
                }
                response.put(CartConstants.ACTION,CartConstants.UPDATED);
                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.TRANSACTED_CART_ITEMS,response);
              })
              .onErrorResume(error -> {
                ACCOUNT_ID.remove();
                log.error(CartLogs.EXCEPTION_TRANSACTING_CART,error,accountId);
                return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_TRANSACT_CART_ITEMS));
              }).block();
          } else {
            products.put(CartConstants.I,i);
            Cart c = mapper.convertValue(products, Cart.class);
            ACCOUNT_ID.set(accountId);
            return cartRepository.save(c)
              .map(resp -> {
                ACCOUNT_ID.remove();
                response.put(CartConstants.COUNT , 1);
                response.put(CartConstants.ACTION,CartConstants.ADDED);
                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ADDED_CART_ITEMS,response);
              })
              .onErrorResume(t -> {
                ACCOUNT_ID.remove();
                if(t instanceof NoSuchIndexException) {
                  log.error(CartLogs.NO_SUCH_INDEX_EXCEPTION,t.getMessage(),ACCOUNT_ID.get());
                }
                if (t instanceof RestStatusException) {
                  // #TODO Check this exception which appears always even if document gets saved properly
                  response.put(CartConstants.COUNT , 1);
                  response.put(CartConstants.ACTION,CartConstants.ADDED);
                  return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ADDED_CART_ITEMS,response));
                }
                return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_ADD_CART_ITEM));
              }).block();
          }
        }
      );
  }
}
