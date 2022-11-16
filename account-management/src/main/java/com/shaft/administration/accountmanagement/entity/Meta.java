package com.shaft.administration.accountmanagement.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "accounts_meta")
public class Meta {
    private int aid;
    private String idx;
    private String name;
}
