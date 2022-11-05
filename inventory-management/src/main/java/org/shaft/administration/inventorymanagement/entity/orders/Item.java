package org.shaft.administration.inventorymanagement.entity.orders;

public class Item {
    private String id;
    private int costPrice;
    private int quantity;
    private String option;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(int costPrice) {
        this.costPrice = costPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", costPrice=" + costPrice +
                ", quantity=" + quantity +
                ", option='" + option + '\'' +
                '}';
    }
}
