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
    private String catalog;
    private String inventory;
    @Field("mappings")
    private Map<String,Routes> routes;
    public AppMapping() {}

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public Map<String,Routes> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String,Routes>  routes) {
        this.routes = routes;
    }

    @Override
    public String toString() {
        return "AppMapping{" +
                "_id='" + _id + '\'' +
                ", catalog='" + catalog + '\'' +
                ", inventory='" + inventory + '\'' +
                ", routes=" + routes +
                '}';
    }
}
