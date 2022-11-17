package com.shaft.administration.accountmanagement.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Getter
@Setter
@Document(indexName = "accounts_meta")
public class Meta {
    @Id
    private String _id = UUID.randomUUID().toString();
    private int aid;
    private String idx;
    private String name;
}
