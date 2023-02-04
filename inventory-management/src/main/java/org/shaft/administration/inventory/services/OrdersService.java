package org.shaft.administration.inventory.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.inventory.clients.RestClient;
import org.shaft.administration.inventory.constants.InventoryConstants;
import org.shaft.administration.inventory.constants.InventoryLogs;
import org.shaft.administration.inventory.dao.OrdersDao;
import org.shaft.administration.inventory.entity.orders.Item;
import org.shaft.administration.inventory.entity.orders.Order;
import org.shaft.administration.inventory.repositories.OrdersRepository;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
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
  private final ObjectMapper mapper = new ObjectMapper();
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
  public Mono<ObjectNode> getOrders(int accountId) {
    ACCOUNT_ID.set(accountId);
    return ordersRepository.findAll()
      .collectList()
      .map(orders -> {
        ACCOUNT_ID.remove();
        return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ORDERS_FETCHED_SUCCESSFULLY,
          mapper.valueToTree(orders));
      })
      .onErrorResume(error -> {
        ACCOUNT_ID.remove();
        log.error(InventoryLogs.UNABLE_TO_FETCH_ORDERS,error,accountId);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.UNABLE_TO_FETCH_ORDERS));
      });
  }

  @Override
  public Mono<ObjectNode> getOrdersForI(int accountId, Map<String,Object> body) {
    if (body.containsKey(InventoryConstants.I)) {
      int i = (int) body.get(InventoryConstants.I);
      ACCOUNT_ID.set(accountId);
      return ordersRepository.findByI(i)
        .collectList()
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(orders -> {
          List<String> itemIds = collectItemsFromOrder(orders);
          return restClient.getProducts(accountId, itemIds)
            .map(productsResponse -> {
              List<Item> items;
              try {
                Map<String, Object> products = mapParser.readValue(productsResponse);
                if (products.containsKey(InventoryConstants.CODE)
                  && ((String) products.get(InventoryConstants.CODE))
                  .startsWith(InventoryConstants.SUCCESS_PREFIX)) {
                  items = (List<Item>) products.get(InventoryConstants.DATA);
                } else {
                  log.error(InventoryLogs.PRODUCT_API_FAILED_CODE,products.get(InventoryConstants.CODE),accountId);
                  return ShaftResponseBuilder.buildResponse(ShaftResponseCode.PRODUCT_API_FAILED);
                }
                // Insert additional information, obtained from items API into specific orders
                if (!items.isEmpty()) {
                  List<Object> response = addItemsMetaToOrders(orders, items);
                  return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ORDERS_FETCHED_SUCCESSFULLY_FOR_I,
                    mapper.valueToTree(response));
                } else {
                  return ShaftResponseBuilder.buildResponse(ShaftResponseCode.NO_ITEMS_IN_ORDER);
                }
              } catch (JsonProcessingException ex) {
                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.INVALID_PRODUCT_API_RESPONSE);
              } catch (Exception ex) {
                log.error(InventoryLogs.EXCEPTION_POPULATING_ITEMS_IN_ORDER,ex,accountId);
                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_POPULATING_ITEMS_IN_ORDER);
              } finally {
                ACCOUNT_ID.remove();
              }
            })
            .onErrorResume(error -> {
              ACCOUNT_ID.remove();
              log.error(InventoryLogs.PRODUCT_API_FAILED,error,accountId);
              return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.PRODUCT_API_ERROR));
            }).block();
        })
        .onErrorResume(error -> {
          ACCOUNT_ID.remove();
          log.error(InventoryLogs.UNABLE_TO_FETCH_ORDERS_FOR_I,error,accountId);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.UNABLE_TO_FETCH_ORDERS_FOR_I));
        });
    } else {
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.NO_I_TO_FETCH_ORDER));
    }
  }

  @Override
  public Mono<ObjectNode> saveOrders(int accountId, Map<String,Object> order) {
    ACCOUNT_ID.set(accountId);
    // #TODO Validate request body by parsing
    Order o = mapper.convertValue(order,Order.class);
    return ordersRepository.save(o)
      .publishOn(Schedulers.boundedElastic())
      .mapNotNull(savedOrder -> {
        // #TODO Add retry mechanism here in case of failure since we have already performed ACID transaction above
        return restClient.emptyCart(accountId,o.getI())
          .map(response -> {
            ObjectNode apiResponse = mapper.createObjectNode();
            try {
              Map<String, Object>  cartAPIResponse = mapParser.readValue(response);
              if (cartAPIResponse.containsKey(InventoryConstants.CODE)
                && ((String) cartAPIResponse.get(InventoryConstants.CODE))
                .startsWith(InventoryConstants.SUCCESS_PREFIX)) {
                JsonNode parsedResponse = mapper.convertValue(cartAPIResponse.get("data"), JsonNode.class);
                boolean success = parsedResponse.has("deleted") && parsedResponse.get("deleted").asBoolean();
                apiResponse.put(InventoryConstants.ACTION,InventoryConstants.ADDED);
                if(success) {
                  apiResponse.put(InventoryConstants.ADDED , true);
                  return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ORDERS_SAVED,apiResponse);
                } else {
                  log.error(InventoryLogs.CART_API_FAILED_CODE,cartAPIResponse.get(InventoryConstants.CODE),accountId);
                  apiResponse.put(InventoryConstants.ADDED , false);
                  return ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_SAVE_ORDER,apiResponse);
                }
              } else {
                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.CART_API_FAILED);
              }
            } catch (JsonProcessingException e) {
              log.error(InventoryLogs.INVALID_CART_API_RESPONSE,e,accountId);
              return ShaftResponseBuilder.buildResponse(ShaftResponseCode.INVALID_CART_API_RESPONSE);
            } finally {
              ACCOUNT_ID.remove();
            }
          }).block();
      })
      .onErrorResume(t -> {
        if (t instanceof NoSuchIndexException) {
          log.error("Exception - {} , No such index {}", t.getMessage(), ACCOUNT_ID.get());
        }
        if (t instanceof RestStatusException) {
          // #TODO Check this exception which appears always even if document gets saved properly
          log.error("RestStatusException {}", t.getMessage(), t);
        }
        ACCOUNT_ID.remove();
      });
  }

  @Override
  public Mono<List<Item>> getBulkItemsInOrder(int accountId, Map<String, Object> itemsInRequest) {
    if (itemsInRequest.containsKey("items")) {
      List<String> itemIds;
      List<Item> convertedItems = mapper.convertValue(itemsInRequest.get("items"), new TypeReference<List<Item>>() {});
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

  private List<Object> addItemsMetaToOrders(List<Order> orders,List<Item> items) {
    List<Object> response = new ArrayList<>();
    for (Order order : orders) {
      Map<String, Object> perOrder = mapper.convertValue(order, new TypeReference<Map<String, Object>>() {});
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
    return response;
  }

  private static List<String> collectItemsFromOrder(List<Order> orders) {
    return orders.stream()
      .flatMap(o -> o.getItems().stream())
      .collect(Collectors.toList()).stream()
      .map(Item::getId)
      .collect(Collectors.toList());
  }
}

