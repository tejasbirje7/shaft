package org.shaft.administration.inventory.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.shaft.administration.inventory.dao.OrdersDao;
import org.shaft.administration.inventory.entity.orders.Item;
import org.shaft.administration.inventory.entity.orders.Order;
import org.shaft.administration.inventory.repositories.OrdersRepository;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdersDAOImpl implements OrdersDao {

    private final OrdersRepository ordersRepository;
    private final RestTemplate restTemplate;
    private HttpHeaders httpHeaders;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public OrdersDAOImpl(OrdersRepository ordersRepository, RestTemplate restTemplate) {
        this.ordersRepository = ordersRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Object> getOrders(int accountId) {
        ACCOUNT_ID.set(accountId);
        // #TODO Handle exceptions
        return Lists.newArrayList(ordersRepository.findAll());
    }

    @Override
    public List<Object> getOrdersForI(int accountId, Map<String,Object> body) {

        ACCOUNT_ID.set(accountId);
        int i;
        if(body.containsKey("i")) {
            i = (int) body.get("i");
        } else {
            // #TODO Throw exception BAD REQUEST
            return new ArrayList<>();
        }

        // Get Orders
        List<Order> orders = ordersRepository.findByI(i);
        List<String> iTemIds = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.toList()).stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        // Invoke catalog API to get more information about items in orders
        Map<String,Object> request = new HashMap<>();
        request.put("items",iTemIds);
        request.put("fields",new String[]{"id","name","description","category","gallery"});
        httpHeaders = new HttpHeaders();
        httpHeaders.set("account",String.valueOf(accountId));
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(request,httpHeaders);
        List<Item> items = new ArrayList<>();
        
        // Invoke API and parse response
        try {
            ResponseEntity<ShaftResponseHandler> response = restTemplate.exchange(
                    "http://localhost:8081/catalog/items/bulk",
                    HttpMethod.POST,entity,ShaftResponseHandler.class);
            items = (List<Item>) response.getBody().getData();
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        // Insert additional information, obtained from items API into specific orders
        List<Object> response = new ArrayList<>();
        try {
            for (int o = 0; o < orders.size(); o++) {
                Map<String,Object> perOrder = objectMapper.convertValue(orders.get(o),new TypeReference<Map<String, Object>>() {});
                List<Item> itemsInOrder = orders.get(o).getItems();
                List<Object> modifyItemsArray = new ArrayList<>();
                for (int it = 0; it < itemsInOrder.size(); it++ ) {
                    Item perItemInOrder = itemsInOrder.get(it);
                    for(int k=0; k < items.size() ; k++) {
                        Map<String,Object> itemFromDB = (Map<String, Object>) items.get(k);
                        if ((itemFromDB.get("id").equals(perItemInOrder.getId()))){
                            itemFromDB.put("costPrice",perItemInOrder.getCostPrice());
                            itemFromDB.put("quantity",perItemInOrder.getQuantity());
                            itemFromDB.put("options",perItemInOrder.getOption());
                            modifyItemsArray.add(itemFromDB);
                        }
                    }
                    perOrder.put("items",modifyItemsArray);
                }
                response.add(perOrder);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            ACCOUNT_ID.remove();
        }
        
        return response;
    }

    @Override
    public boolean saveOrders(int accountId, Map<String,Object> order) {
        ACCOUNT_ID.set(accountId);

        // Validate request body by parsing
        //Order o = (Order) order;
        Order o = objectMapper.convertValue(order,Order.class);
        try {
            // Insert order to database
            ordersRepository.save(o);

            // #TODO Add retry mechanism here in case of failure since we have already performed ACID transaction above
            // Remove orders from cart
            httpHeaders = new HttpHeaders();
            httpHeaders.set("account",String.valueOf(accountId));
            httpHeaders.set("i",String.valueOf(o.getI()));
            httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<ShaftResponseHandler> entity = new HttpEntity<ShaftResponseHandler>(httpHeaders);

            // Invoke API and parse response
            try {
                ResponseEntity<ShaftResponseHandler> response = restTemplate.exchange(
                        "http://localhost:8083/cart/empty",
                        HttpMethod.GET,entity,ShaftResponseHandler.class);
                ShaftResponseHandler data = response.getBody();
            } catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        } catch (Exception ex) {
            return false;
        } finally {
            ACCOUNT_ID.remove();
        }
        return true;
    }

    @Override
    public List<Object> getBulkItemsInOrder(int accountId, Map<String, Object> itemsInRequest) {
        ACCOUNT_ID.set(accountId);
        // Invoke catalog API to get more information about items in orders
        Map<String, Object> request = new HashMap<>();
        List<String> itemIds = null;
        if (itemsInRequest.containsKey("items")) {
            List<Item> convertedItems = objectMapper.convertValue(itemsInRequest.get("items"), new TypeReference<List<Item>>() {});
            itemIds = convertedItems.stream()
                    .map(Item::getId)
                    .collect(Collectors.toList());
        } else {
            // #TODO Throw BAD_REQUEST exception
        }
        request.put("items", itemIds);
        request.put("fields", new String[]{"id", "name", "description", "category", "gallery"});
        List<Object> items;
        // Invoke API and parse response
        try {
            httpHeaders = new HttpHeaders();
            httpHeaders.set("account", String.valueOf(accountId));
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, httpHeaders);
            ResponseEntity<ShaftResponseHandler> response = restTemplate.exchange(
                    "http://localhost:8081/catalog/items/bulk",
                    HttpMethod.POST, entity, ShaftResponseHandler.class);
            items = (List<Object>) response.getBody().getData();
        } catch (Exception ex) {
            items = new ArrayList<>();
            System.out.println(ex.getMessage());
        } finally {
            ACCOUNT_ID.remove();
        }
        return items;
    }

    @Override
    public boolean updateOrdersStage(int accountId, Map<String, Object> status) {
        if (status.containsKey("oid") && status.containsKey("sg")) {
            Long resp = ordersRepository.updateOrderStage((String) status.get("oid"), (Integer) status.get("sg"));
            return resp > 0;
        } else {
            // #TODO Throw Bad exception
        }
        return false;
    }
}

