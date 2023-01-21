package org.shaft.administration.inventory.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.inventory.clients.RestClient;
import org.shaft.administration.inventory.dao.OrdersDao;
import org.shaft.administration.inventory.entity.orders.Item;
import org.shaft.administration.inventory.entity.orders.Order;
import org.shaft.administration.inventory.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersService implements OrdersDao {

  private final OrdersRepository ordersRepository;
  private final RestClient restClient;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ObjectReader mapParser;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }

  @Autowired
  public OrdersService(OrdersRepository ordersRepository, RestClient restClient) {
    this.ordersRepository = ordersRepository;
    this.restClient = restClient;
    mapParser = new ObjectMapper().readerFor(Map.class);
  }

  @Override
  public Mono<List<Order>> getOrders(int accountId) {
    ACCOUNT_ID.set(accountId);
    // #TODO Handle exceptions
    return ordersRepository.findAll().collectList()
      .doOnNext( t -> ACCOUNT_ID.remove());
  }

  @Override
  public Mono<List<Object>> getOrdersForI(int accountId, Map<String,Object> body) {
    int i;
    if (body.containsKey("i")) {
      i = (int) body.get("i");
      ACCOUNT_ID.set(accountId);
      return ordersRepository.findByI(i)
        .collectList()
        .publishOn(Schedulers.boundedElastic())
        .map(orders -> {
          List<String> itemIds = orders.stream()
            .flatMap(o -> o.getItems().stream())
            .collect(Collectors.toList()).stream()
            .map(Item::getId)
            .collect(Collectors.toList());
          List<Object> response = new ArrayList<>();
          restClient.getProducts(accountId, itemIds)
            .doOnSuccess(productsResponse -> {
              List<Item> items = new ArrayList<>();
              try {
                Map<String, Object> products = mapParser.readValue(productsResponse);
                if (products.containsKey("code") && ((String) products.get("code")).startsWith("S")) {
                  items = (List<Item>) products.get("data");
                }
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
              // Insert additional information, obtained from items API into specific orders
              if (!items.isEmpty()) {
                try {
                  for (Order order : orders) {
                    Map<String, Object> perOrder = objectMapper.convertValue(order, new TypeReference<Map<String, Object>>() {
                    });
                    List<Item> itemsInOrder = order.getItems();
                    List<Object> modifyItemsArray = new ArrayList<>();
                    for (Item perItemInOrder : itemsInOrder) {
                      for (Item item : items) {
                        Map<String, Object> itemFromDB = (Map<String, Object>) item;
                        if ((itemFromDB.get("id").equals(perItemInOrder.getId()))) {
                          itemFromDB.put("costPrice", perItemInOrder.getCostPrice());
                          itemFromDB.put("quantity", perItemInOrder.getQuantity());
                          itemFromDB.put("options", perItemInOrder.getOption());
                          modifyItemsArray.add(itemFromDB);
                        }
                      }
                      perOrder.put("items", modifyItemsArray);
                    }
                    response.add(perOrder);
                  }
                } catch (Exception ex) {
                  System.out.println(ex.getMessage());
                } finally {
                  ACCOUNT_ID.remove();
                }
              }
            }).block();
          ACCOUNT_ID.remove();
          return response;
        });
    }
    else {
      // #TODO Throw exception BAD REQUEST
      return Mono.empty();
    }
  }

  @Override
  public Mono<Order> saveOrders(int accountId, Map<String,Object> order) {
    ACCOUNT_ID.set(accountId);
    // #TODO Validate request body by parsing
    Order o = objectMapper.convertValue(order,Order.class);
    return ordersRepository.save(o)
      .doOnError(t -> {
        if (t instanceof NoSuchIndexException) {
          log.error("Exception - {} , No such index {}", t.getMessage(), ACCOUNT_ID.get());
        }
        if (t instanceof RestStatusException) {
          // #TODO Check this exception which appears always even if document gets saved properly
          log.error("RestStatusException {}", t.getMessage(), t);
        }
        ACCOUNT_ID.remove();
      })
      .publishOn(Schedulers.boundedElastic())
      .doOnSuccess(savedOrder -> {
        // #TODO Add retry mechanism here in case of failure since we have already performed ACID transaction above
        restClient.emptyCart(accountId,o.getI())
          .map(response -> {
            Order returnOrder = new Order();
            try {
              Map<String, Object> checkIfSuccess = mapParser.readValue(response);
              if (checkIfSuccess.containsKey("code") && ((String) checkIfSuccess.get("code")).startsWith("S")) {
                boolean success = (boolean) checkIfSuccess.get("data");
                if(success) {
                  returnOrder = o;
                }
              }
            } catch (JsonProcessingException e) {
              throw new RuntimeException(e);
            }
            return returnOrder;
          }).block();
        ACCOUNT_ID.remove();
      });
  }

  @Override
  public Mono<List<Item>> getBulkItemsInOrder(int accountId, Map<String, Object> itemsInRequest) {
    if (itemsInRequest.containsKey("items")) {
      List<String> itemIds;
      List<Item> convertedItems = objectMapper.convertValue(itemsInRequest.get("items"), new TypeReference<List<Item>>() {});
      itemIds = convertedItems.stream()
        .map(Item::getId)
        .collect(Collectors.toList());
      return restClient.getProducts(accountId, itemIds)
        .map(productsResponse -> {
          List<Item> items = new ArrayList<>();
          try {
            Map<String, Object> products = mapParser.readValue(productsResponse);
            if (products.containsKey("code") && ((String) products.get("code")).startsWith("S")) {
              items = (List<Item>) products.get("data");
            }
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
          return items;
        });
    } else {
      // #TODO Throw BAD_REQUEST exception
    }
    return Mono.empty();
  }

  @Override
  public Mono<Map<String,Long>> updateOrdersStage(int accountId, Map<String, Object> status) {
    Map<String,Long> response = new HashMap<>();
    if (status.containsKey("oid") && status.containsKey("sg")) {
      return ordersRepository.updateOrderStage((Integer) status.get("oid"), (Integer) status.get("sg"))
        .map(totalUpdated -> {
          response.put("updated",totalUpdated);
          return response;
        });
    } else {
      // #TODO Throw Bad exception
      response.put("updated",0L);
    }
    return Mono.just(response);
  }
}

