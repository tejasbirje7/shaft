package org.shaft.administration.catalog.entity.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document(indexName = "#{T(org.shaft.administration.catalog.services.CategoryDAOImpl).getAccount()}_category")
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
