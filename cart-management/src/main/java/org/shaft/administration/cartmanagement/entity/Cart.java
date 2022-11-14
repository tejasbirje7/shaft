package org.shaft.administration.cartmanagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Document(indexName = "#{T(org.shaft.administration.catalog.services.ItemsDAOImpl).getAccount()}_cart")
public class Cart {
    @Id
    private String _id = UUID.randomUUID().toString();
    private int i;
    private List<Products> products;

}
