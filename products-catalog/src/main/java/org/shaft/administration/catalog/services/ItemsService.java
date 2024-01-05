package org.shaft.administration.catalog.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileNotFoundException;
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

  // #TODO Remove account ID dependency from all services
  @Override
  public Mono<ObjectNode> getItemsById(int accountId, Map<String,Object> body) {
    ACCOUNT_ID.set(accountId);
    String itemId = (String) body.get("id");
    Mono<Item> itemInfo = itemsRepository.getItemById(itemId);
    return itemInfo
      .map(i -> {
        ACCOUNT_ID.remove();
        log.info("Item : {}",i);
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
          JsonNode r = mapper.valueToTree(i);
          return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ITEMS_FETCHED_SUCCESSFULLY,r);
        })
        .onErrorResume(error -> {
          ACCOUNT_ID.remove();
          log.error(ProductCatalogLogs.UNABLE_TO_FETCH_ITEMS,error);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_WHILE_FETCHING_BULK_ITEMS));
        });
    } else {
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BAD_BULK_ITEMS_REQUEST));
    }
  }

  @Override
  public Mono<ObjectNode> saveItem(int accountId, Map<String, Object> body, FilePart image) {
    // #TODO Save the image item files in S3
    ACCOUNT_ID.set(accountId);
    return itemsRepository.save(createItemPojoFromRequest(body))
      .flatMap(i -> saveAssets(image))
      .onErrorResume(error -> {
        ACCOUNT_ID.remove();
        if(isRestStatusException(error)) {
          return saveAssets(image);
        } /*else if (error instanceof FileNotFoundException) {
          // TODO Throw exception if image is not saved from above map

        } */else {
          log.error(ProductCatalogLogs.SHAFT_ITEMS_SAVE_EXCEPTION,error);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_ITEMS_SAVE_EXCEPTION));
        }
      });
  }

  public Mono<ObjectNode> deleteItem(int accountId, Map<String, Object> body) {
    ACCOUNT_ID.set(accountId);
    return itemsRepository.deleteById(createItemPojoFromRequest(body).getId())
      .map(response -> {
        log.info("Received response : {}",response);
        return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ITEM_DELETED);
      })
      .onErrorResume(error -> {
        log.error("Error : {}",error);
        if(isRestStatusException(error)) {
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ITEM_DELETED));
        } else {
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_DELETE_ITEM));
        }
      });
  }

  public Mono<ObjectNode> saveAssets(FilePart image) {
    ACCOUNT_ID.remove();
    log.info("File name: {}", image.filename());
    return image.transferTo(new File("/opt/shop_assets/1600/store",image.filename()))
      .map(r -> ShaftResponseBuilder.buildResponse(ShaftResponseCode.ITEMS_SAVED_SUCCESSFULLY))
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
