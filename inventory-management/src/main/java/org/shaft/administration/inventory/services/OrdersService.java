package org.shaft.administration.inventory.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
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
        // #TODO Add retry mechanism here in case of failure since we have already performed ACID transaction above otherwise revert changes
        return restClient.emptyCart(accountId,o.getI())
          .map(response -> {
            ObjectNode apiResponse = mapper.createObjectNode();
            try {
              Map<String, Object>  cartAPIResponse = mapParser.readValue(response);
              if (cartAPIResponse.containsKey(InventoryConstants.CODE)
                && ((String) cartAPIResponse.get(InventoryConstants.CODE))
                .startsWith(InventoryConstants.SUCCESS_PREFIX)) {
                JsonNode parsedResponse = mapper.convertValue(cartAPIResponse.get(InventoryConstants.DATA), JsonNode.class);
                boolean success = parsedResponse.has(InventoryConstants.DELETED)
                  && parsedResponse.get(InventoryConstants.DELETED).asBoolean();
                apiResponse.put(InventoryConstants.ACTION,InventoryConstants.ADDED);
                if(success) {
                  apiResponse.put(InventoryConstants.ADDED , true);
                  return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ORDERS_SAVED,apiResponse);
                } else {
                  log.error(InventoryLogs.CART_API_FAILED_CODE,cartAPIResponse.get(InventoryConstants.CODE),accountId);
                  apiResponse.put(InventoryConstants.ADDED , false);
                  return ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_REMOVE_CART_ITEMS,apiResponse);
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
          })
          .onErrorResume(error -> {
            ACCOUNT_ID.remove();
            log.error(InventoryLogs.CART_API_ERROR,error,ACCOUNT_ID.get());
            return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.CART_API_ERROR));
          }).block();
      })
      .onErrorResume(t -> {
        ACCOUNT_ID.remove();
        if (t instanceof RestStatusException) {
          ObjectNode apiResponse = mapper.createObjectNode();
          apiResponse.put(InventoryConstants.ADDED , true);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ORDERS_SAVED,apiResponse));
        } else {
          log.error(InventoryLogs.EXCEPTION_SAVING_ORDER, t.getMessage(), ACCOUNT_ID.get());
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_SAVE_ORDER));
        }
      });
  }

  @Override
  public Mono<ObjectNode> getBulkItemsInOrder(int accountId, Map<String, Object> itemsInRequest) {
    if (itemsInRequest.containsKey(InventoryConstants.ITEMS)) {
      List<String> itemIds;
      List<Item> convertedItems = mapper.convertValue(itemsInRequest.get(InventoryConstants.ITEMS),
        new TypeReference<List<Item>>() {});
      itemIds = convertedItems.stream()
        .map(Item::getId)
        .collect(Collectors.toList());
      return restClient.getProducts(accountId, itemIds)
        .map(productsResponse -> {
          List<Item> items;
          try {
            Map<String, Object> products = mapParser.readValue(productsResponse);
            if (products.containsKey(InventoryConstants.CODE)
              && ((String) products.get(InventoryConstants.CODE)).startsWith(InventoryConstants.SUCCESS_PREFIX)) {
              items = (List<Item>) products.get(InventoryConstants.DATA);
              return ShaftResponseBuilder.buildResponse(ShaftResponseCode.FETCHED_BULK_ITEMS_IN_ORDER,mapper.valueToTree(items));
            } else {
              log.error(InventoryLogs.BULK_PRODUCT_API_FAILED,products.get(InventoryConstants.CODE),accountId);
              return ShaftResponseBuilder.buildResponse(ShaftResponseCode.BULK_PRODUCT_API_FAILED);
            }
          } catch (JsonProcessingException e) {
            log.error(InventoryLogs.BULK_PRODUCT_API_INVALID_RESPONSE,e,accountId);
            return ShaftResponseBuilder.buildResponse(ShaftResponseCode.BULK_PRODUCT_API_INVALID_RESPONSE);
          } catch (Exception ex){
            log.error(InventoryLogs.EXCEPTION_FETCHING_BULK_ITEMS,ex,accountId);
            return ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_WHILE_FETCHING_BULK_ITEMS);
          }
        })
        .onErrorResume(error -> {
          log.error(InventoryLogs.BULK_PRODUCT_API_ERROR,error,accountId);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BULK_PRODUCT_API_ERROR));
        });
    } else {
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BAD_REQUEST_FOR_BULK_ITEMS));
    }
  }

  @Override
  public Mono<ObjectNode> updateOrdersStage(int accountId, Map<String, Object> status) {
    ObjectNode response = mapper.createObjectNode();
    if (status.containsKey(InventoryConstants.ORDER_ID) && status.containsKey(InventoryConstants.STAGE)) {
      return ordersRepository.updateOrderStage((Integer) status.get(InventoryConstants.ORDER_ID),
          (Integer) status.get(InventoryConstants.STAGE),
          accountId)
        .map(totalUpdated -> {
          if(totalUpdated > 0) {
            response.put(InventoryConstants.UPDATED,true);
            return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ORDER_STAGE_UPDATED,response);
          } else {
            log.error(InventoryLogs.FAILED_TO_UPDATE_ORDER_STAGE,accountId);
            response.put(InventoryConstants.UPDATED,false);
            return ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_UPDATE_ORDER_STAGE);
          }
        })
        .onErrorResume(error -> {
          log.error(InventoryLogs.EXCEPTION_UPDATING_ORDER_STAGE,error,accountId);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_UPDATING_ORDER_STAGE));
        });
    } else {
      log.error(InventoryLogs.BAD_REQUEST_FOR_UPDATING_ORDER_STAGE, accountId);
      response.put(InventoryConstants.UPDATED,false);
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BAD_UPDATE_ORDER_STAGE_REQUEST));
    }
  }

  private List<Object> addItemsMetaToOrders(List<Order> orders,List<Item> items) {
    List<Object> response = new ArrayList<>();
    ArrayNode k = mapper.valueToTree(items);
    for (Order order : orders) {
      Map<String, Object> perOrder = mapper.convertValue(order, new TypeReference<Map<String, Object>>() {});
      List<Item> itemsInOrder = order.getItems();
      List<Object> modifyItemsArray = new ArrayList<>();
      for (Item perItemInOrder : itemsInOrder) {
        for (JsonNode item : k) {
          ObjectNode itemFromDB = (ObjectNode) item;
          if ((itemFromDB.get(InventoryConstants.ID).asText().equals(perItemInOrder.getId()))) {
            itemFromDB.put(InventoryConstants.COST_PRICE, perItemInOrder.getCostPrice());
            itemFromDB.put(InventoryConstants.QUANTITY, perItemInOrder.getQuantity());
            itemFromDB.put(InventoryConstants.OPTIONS, perItemInOrder.getOption());
            modifyItemsArray.add(itemFromDB);
          }
        }
        perOrder.put(InventoryConstants.ITEMS, modifyItemsArray);
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

