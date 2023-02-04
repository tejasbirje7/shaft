package org.shaft.administration.catalog.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.catalog.constants.ProductCatalogConstants;
import org.shaft.administration.catalog.constants.ProductCatalogLogs;
import org.shaft.administration.catalog.dao.ItemsDAO;
import org.shaft.administration.catalog.entity.item.Item;
import org.shaft.administration.catalog.repositories.ItemsRepository;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ItemsService implements ItemsDAO {

  private final ItemsRepository itemsRepository;

  private final WebClient webClient;

  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

  public ObjectMapper mapper = new ObjectMapper();

  public static int getAccount() {
    return ACCOUNT_ID.get();
  }

  @Autowired
  public ItemsService(ItemsRepository itemsRepository, WebClient webClient) {
    this.itemsRepository = itemsRepository;
    this.webClient = webClient;
  }

  /**
   * #TODO Implement Cache here
   * @param accountId
   * @return List
   */
  @Override
  public Mono<ObjectNode> getItems(int accountId, Map<String,Object> body) {
    ACCOUNT_ID.set(accountId);
    Flux<Item> itemsFlux;
    if (body!=null && body.containsKey(ProductCatalogConstants.FIELDS)) {
      String[] fields = ((ArrayList<String>)body.get(ProductCatalogConstants.FIELDS)).toArray(new String[0]);
      // #TODO Check if items are inStock and return those for end user. For dashboard users all items will be populated
      itemsFlux = itemsRepository.getItemsWithSource(fields);
    } else {
      itemsFlux = itemsRepository.findAll();
    }
    return itemsFlux
      .collectList()
      .map(i -> {
        ACCOUNT_ID.remove();
        return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ITEMS_FETCHED_SUCCESSFULLY,mapper.valueToTree(i));
      })
      .onErrorResume(error -> {
        ACCOUNT_ID.remove();
        log.error(ProductCatalogLogs.UNABLE_TO_FETCH_ITEMS,error);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_ITEMS_SERVICE_UNAVAILABLE));
      });
  }

  @Override
  public Mono<ObjectNode> getBulkItems(int accountId, Map<String,Object> body) {
    if(body != null) {
      Flux<Item> itemsFlux;
      List<String> itemIds = (List<String>) body.get(ProductCatalogConstants.ITEMS);
      if (body.containsKey(ProductCatalogConstants.FIELDS)) {
        String[] fields = ((ArrayList<String>)body.get(ProductCatalogConstants.FIELDS)).toArray(new String[0]);
        itemsFlux = itemsRepository.getItemsWithSource(itemIds,fields);
      } else {
        itemsFlux = itemsRepository.findByIdIn(itemIds);
      }
      ACCOUNT_ID.set(accountId);
      return itemsFlux
        .collectList()
        .map(i -> {
          ACCOUNT_ID.remove();
          return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ITEMS_FETCHED_SUCCESSFULLY,mapper.valueToTree(i));
        })
        .onErrorResume(error -> {
          ACCOUNT_ID.remove();
          log.error(ProductCatalogLogs.UNABLE_TO_FETCH_ITEMS,error);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_ITEMS_SERVICE_UNAVAILABLE));
        });
    } else {
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BAD_BULK_ITEMS_REQUEST));
    }
  }

  @Override
  public Mono<ObjectNode> saveItem(int accountId, Map<String, Object> body) {
    // #TODO Save the image item files in S3
    ACCOUNT_ID.set(accountId);

    return itemsRepository.save(createItemPojoFromRequest(body))
      .map(i -> {
        ACCOUNT_ID.remove();
        return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ITEMS_SAVED_SUCCESSFULLY,
          mapper.convertValue(i, ObjectNode.class));
      })
      .onErrorResume(error -> {
        ACCOUNT_ID.remove();
        log.error(ProductCatalogLogs.SHAFT_ITEMS_SAVE_EXCEPTION,error);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_ITEMS_SAVE_EXCEPTION));
      });
  }

  public Item createItemPojoFromRequest(Map<String,Object> itemDetails) {
    // #TODO Get all these details from UI, don't replicate here and remove this code
    itemDetails.put(ProductCatalogConstants.DETAIL,
      itemDetails.get(ProductCatalogConstants.DESCRIPTION));
    itemDetails.put(ProductCatalogConstants.GALLERY,
      new String[]{(String) itemDetails.get(ProductCatalogConstants.IMG)});
    itemDetails.put(ProductCatalogConstants.ON_SALE,false);
    itemDetails.put(ProductCatalogConstants.IN_STOCK,true);
    // #TODO Replace item id's to be epoch instead of base64 strings
    if(!itemDetails.containsKey(ProductCatalogConstants.ID)) {
      // #TODO Handling update case in save items. Isolate both flows differently. If case is update, id will come from ui
      byte[] bytesEncoded = Base64.encodeBase64(((String)itemDetails.get(ProductCatalogConstants.NAME)).getBytes());
      itemDetails.put(ProductCatalogConstants.ID,new String(bytesEncoded));
    }
    return mapper.convertValue(itemDetails, new TypeReference<Item>() {});
  }

  private boolean isRestStatusException(Throwable t) {
    return t instanceof RestStatusException;
  }
}
