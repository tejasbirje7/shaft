package org.shaft.administration.catalog.entity.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{T(org.shaft.administration.catalog.services.ItemsService).getAccount()}_items")
public class Item {

    /*
    {
  "name": "Peeled Garlic",
  "category": "Vegetables",
  "discountedPrice": 0,
  "costPrice": 40,
  "description": "Garlic Cloves",
  "qt": 12,
  "options": [
    "xs",
    "s",
    "m",
    "l",
    "xl"
  ],
  "inStock": true,
  "onSale": "",
  "img": "IMG_1649.jpg"
}
     */

    @Field("id")
    @Id
    private String id;
    @Field("name")
    private String name;
    @Field("description")
    private String description;
    @Field("detail")
    private String detail;
    @Field("category")
    private String category;
    @Field("img")
    private String img;
    @Field("gallery")
    private List<String> gallery;
    @Field("onSale")
    private boolean onSale;
    @Field("costPrice")
    private int costPrice;
    @Field("inStock")
    private boolean inStock;
    @Field("options")
    private List<String> options;
    @Field("qt")
    private int qt;

}
