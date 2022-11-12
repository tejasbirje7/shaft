package org.shaft.administration.apigateway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Document(indexName = "app_mappings")
public class AppMapping {
    @Id
    private String _id = UUID.randomUUID().toString();
    private String catalog;
    private String inventory;
    private String cart;
    @Field("mappings")
    private Map<String,Routes> routes;
}
