package org.shaft.administration.catalog.entity.category;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

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

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    @Override
    public String toString() {
        return "Category{" +
                "cid=" + cid +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", redirect='" + redirect + '\'' +
                '}';
    }
}
