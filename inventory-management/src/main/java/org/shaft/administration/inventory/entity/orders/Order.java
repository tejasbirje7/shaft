package org.shaft.administration.inventory.entity.orders;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Document(indexName = "#{T(org.shaft.administration.inventory.services.OrdersDAOImpl).getAccount()}_orders")
public class Order {
    @Id
    private int oid;
    @Field("i")
    private int i;
    private int ts;
    private double totalPrice;
    private int sg;
    private boolean st;
    private CustomerInfo customerInfo;
    private DeliveryInfo deliveryInfo;
    private PaymentInfo paymentInfo;
    private List<Item> items;

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getSg() {
        return sg;
    }

    public void setSg(int sg) {
        this.sg = sg;
    }

    public boolean isSt() {
        return st;
    }

    public void setSt(boolean st) {
        this.st = st;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "oid=" + oid +
                ", i=" + i +
                ", ts=" + ts +
                ", totalPrice=" + totalPrice +
                ", sg=" + sg +
                ", st=" + st +
                ", customerInfo=" + customerInfo +
                ", deliveryInfo=" + deliveryInfo +
                ", paymentInfo=" + paymentInfo +
                ", items=" + items +
                '}';
    }
}
