package org.shaft.administration.catalog.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.catalog.dao.ItemsDAO;
import org.shaft.administration.catalog.entity.item.Item;
import org.shaft.administration.catalog.repositories.items.ItemsCustomRepositoryImpl;
import org.shaft.administration.catalog.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemsDAOImpl implements ItemsDAO {

    private final ItemsRepository itemsRepository;

    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

    public ObjectMapper mapper = new ObjectMapper();

    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public ItemsDAOImpl(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    /**
     * #TODO Implement Cache here
     * @param accountId
     * @return List
     */
    @Override
    public List<Item> getItems(int accountId, Map<String,Object> body) {
        ACCOUNT_ID.set(accountId);
        try {
            if (body!=null && body.containsKey("fields")) {
                String[] fields = ((ArrayList<String>)body.get("fields")).toArray(new String[0]);
                // #TODO Check if items are inStock and return those for end user. For dashboard users all items will be populated
                return itemsRepository.getItemsWithSource(fields);
            } else {
                return Lists.newArrayList(itemsRepository.findAll());
            }
        } catch (Exception ex) {
            System.out.println("Exception "+ ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Item> getBulkItems(int accountId, Map<String,Object> body) {
        ACCOUNT_ID.set(accountId);
        try {
            if(body != null) {
                List<String> itemIds = (List<String>) body.get("items");
                if (body.containsKey("fields")) {
                    String[] fields = ((ArrayList<String>)body.get("fields")).toArray(new String[0]);
                    return itemsRepository.getItemsWithSource(itemIds,fields);
                } else {
                    return Lists.newArrayList(itemsRepository.findByIdIn(itemIds));
                }
            } else {
                // #TODO Throw BAD_REQUEST exception
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            System.out.println("Exception "+ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Item saveItem(int accountId, Map<String, Object> body) {

        // #TODO Save the image item files in S3
        ACCOUNT_ID.set(accountId);
        Item savedItem = new Item();
        try {
            // #TODO Remove createItemPojoFromRequest dependency
             savedItem = itemsRepository.save(createItemPojoFromRequest(body));
        } catch (NoSuchIndexException ex) {
            // #TODO Throw internal error exception. Handle NoSuchIndexException exception for all services which is usually raised in case of no index present
            System.out.printf(ex.getMessage());
        } catch (Exception ex) {
            System.out.printf(ex.getMessage());
        } finally {
            ACCOUNT_ID.remove();
        }
        //ACCOUNT_ID.remove();
        return savedItem;
    }

    public Item createItemPojoFromRequest(Map<String,Object> body) {

        // #TODO Get all these details from UI, don't replicate here and remove this code
        Map<String,Object> itemDetails = null;
        try {
            itemDetails = mapper.readValue((String) body.get("itemDetails"), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        itemDetails.put("detail",itemDetails.get("description"));
        itemDetails.put("gallery",new String[]{(String) itemDetails.get("img")});
        itemDetails.put("onSale",false);
        itemDetails.put("inStock",true);
        // #TODO Replace item id's to be epoch instead of base64 strings
        if(!itemDetails.containsKey("id")) {
            // #TODO Handling update case in save items. Isolate both flows differently. If case is update, id will come from ui
            byte[] bytesEncoded = Base64.encodeBase64(((String)itemDetails.get("name")).getBytes());
            itemDetails.put("id",new String(bytesEncoded));
        }
        return mapper.convertValue(itemDetails, new TypeReference<Item>() {});
    }
}
