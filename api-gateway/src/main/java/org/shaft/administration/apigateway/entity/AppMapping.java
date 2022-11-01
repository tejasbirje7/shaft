package org.shaft.administration.apigateway.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Map;
import java.util.UUID;

@Document(indexName = "app_mappings")
public class AppMapping {
    @Id
    private String _id = UUID.randomUUID().toString();
    private String basePath;
    private String ingestionUrl;
    @Field("mappings")
    private Map<String,Object> routes;

    public AppMapping() {}

    public AppMapping(String basePath, String ingestionUrl, Map<String, Object> routes) {
        this.basePath = basePath;
        this.ingestionUrl = ingestionUrl;
        this.routes = routes;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getIngestionUrl() {
        return ingestionUrl;
    }

    public void setIngestionUrl(String ingestionUrl) {
        this.ingestionUrl = ingestionUrl;
    }

    public Map<String, Object> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, Object> routes) {
        this.routes = routes;
    }

    @Override
    public String toString() {
        return "AppMapping{" +
                "basePath='" + basePath + '\'' +
                ", ingestionUrl='" + ingestionUrl + '\'' +
                ", mappings=" + routes +
                '}';
    }
}
