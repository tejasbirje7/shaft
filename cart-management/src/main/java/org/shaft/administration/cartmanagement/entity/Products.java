package org.shaft.administration.cartmanagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class Products {
    private Product product;
    private int quantity;
    private Map<String,String> option;
}
