package org.shaft.administration.inventorymanagement.services;

import com.google.common.collect.Lists;
import org.shaft.administration.inventorymanagement.dao.OrdersDao;
import org.shaft.administration.inventorymanagement.entity.orders.Item;
import org.shaft.administration.inventorymanagement.entity.orders.Order;
import org.shaft.administration.inventorymanagement.repositories.CustomRepository;
import org.shaft.administration.inventorymanagement.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersDAOImpl implements OrdersDao {

    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    CustomRepository customRepository;

    @Override
    public List<Order> getOrdersForI(int accountId, int i) {
        List<Order> orders = ordersRepository.findByI(i);
        List<String> iTemIds = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.toList()).stream()
                .map(items -> items.getId())
                .collect(Collectors.toList());
        System.out.println(iTemIds);
        return ordersRepository.findByI(i);
    }

    /*
    @Override
    public List<Order> getOrders(int accountId, int i) {
        try {
            ArrayList<Integer> arr = new ArrayList<>();
            arr.add(1656863920);
            //arr.add(1656863920);
            List<Order> o = customRepository.getOrders(arr);
            return o;
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }*/
}
