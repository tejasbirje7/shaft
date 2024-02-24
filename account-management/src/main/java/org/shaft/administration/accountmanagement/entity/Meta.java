package org.shaft.administration.accountmanagement.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Document(indexName = "accounts_meta",createIndex = false)
public class Meta {
    @Id
    private int aid;
    private String idx;
    private String name;
    private Map<String,String> dashboardQueries;
    private String templateId;
}
