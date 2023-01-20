package org.shaft.administration.cartmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
