package org.shaft.administration.catalog.entity.category;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = "#{T(org.shaft.administration.catalog.services.CategoryService).getAccount()}_category")
public class Category {
    @Id
    private int cid;
    @Field("description")
    private String description;
    @Field("name")
    private String name;
    @Field("redirect")
    private String redirect;
}
