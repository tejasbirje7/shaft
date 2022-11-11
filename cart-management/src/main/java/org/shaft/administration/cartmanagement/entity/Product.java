package org.shaft.administration.cartmanagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class Product {
    private String id;
    private String name;
    private String description;
    private String detail;
    private String category;
    private String img;
    private boolean onSale;
    private double costPrice;
    private boolean inStock;
    @Field(name = "qt")
    private int quantity;
}
