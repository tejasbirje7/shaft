package org.shaft.administration.inventory.entity.items;

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
public class Item {
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
    private String image;
    @Field("gallery")
    private List<String> gallery;
    @Field("onSale")
    private boolean onSale;
    @Field("costPrice")
    private int costPrice;
    @Field("inStock")
    private boolean inStock;
    @Field("options")
    private List<ItemOptions> options;
    @Field("qt")
    private int quantity;

}
