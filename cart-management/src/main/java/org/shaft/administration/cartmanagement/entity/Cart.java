package org.shaft.administration.cartmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{T(org.shaft.administration.cartmanagement.services.CartService).getAccount()}_cart",createIndex = false)
public class Cart {
    @Id
    private int i;
    private List<Products> products;

}
