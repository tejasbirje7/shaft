package org.shaft.administration.cartmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Map;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Products {
    private Product product;
    private int quantity;
    private Map<String,String> option;
}
