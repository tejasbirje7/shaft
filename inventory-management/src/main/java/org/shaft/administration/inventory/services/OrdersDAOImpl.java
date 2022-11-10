package org.shaft.administration.inventory.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.shaft.administration.inventory.dao.OrdersDao;
import org.shaft.administration.inventory.entity.orders.Item;
import org.shaft.administration.inventory.entity.orders.Order;
import org.shaft.administration.inventory.repositories.CustomRepository;
import org.shaft.administration.inventory.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdersDAOImpl implements OrdersDao {

    private OrdersRepository ordersRepository;
    private CustomRepository customRepository;
    private RestTemplate httpFactory;
    private HttpHeaders httpHeaders;
    private ObjectMapper objectMapper = new ObjectMapper();
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public OrdersDAOImpl(OrdersRepository ordersRepository, CustomRepository customRepository, RestTemplate httpFactory) {
        this.ordersRepository = ordersRepository;
        this.customRepository = customRepository;
        this.httpFactory = httpFactory;
    }

    @Override
    public List<Object> getOrdersForI(int accountId, int i) {

        ACCOUNT_ID.set(accountId);

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
        List<Object> items = new ArrayList<>();
        
        // Invoke API and parse response
        try {
            ResponseEntity<ShaftResponseHandler> response = httpFactory.exchange(
                    "http://localhost:8081/catalog/items/bulk",
                    HttpMethod.POST,entity,ShaftResponseHandler.class);
            items = (List<Object>) response.getBody().getData();
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
                        if ((itemFromDB.get("_id").equals(perItemInOrder.getId()))){
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

            // Remove orders from cart
            httpHeaders = new HttpHeaders();
            httpHeaders.set("account",String.valueOf(accountId));
            httpHeaders.set("i",String.valueOf(o.getI()));
            httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<ShaftResponseHandler> entity = new HttpEntity<ShaftResponseHandler>(httpHeaders);

            // Invoke API and parse response
            try {
                ResponseEntity<ShaftResponseHandler> response = httpFactory.exchange(
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
}
