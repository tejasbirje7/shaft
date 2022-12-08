package org.shaft.administration.inventory.entity.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Document(indexName = "#{T(org.shaft.administration.inventory.services.OrdersDAOImpl).getAccount()}_orders")
public class Order {
    @Id
    private int oid;
    @Field("i")
    private int i;
    private int ts;
    private double totalPrice;
    private int sg; // #TODO Replace this with ENUM
    private CustomerInfo customerInfo;
    private DeliveryInfo deliveryInfo;
    private PaymentInfo paymentInfo;
    private List<Item> items;

}
