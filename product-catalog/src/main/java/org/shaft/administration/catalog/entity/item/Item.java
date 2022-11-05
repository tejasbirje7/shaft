package org.shaft.administration.catalog.entity.item;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Document(indexName = "#{T(org.shaft.administration.catalog.services.ItemsDAOImpl).getAccount()}_items")
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

    public String get_id() {
        return id;
    }

    public void set_id(String _id) {
        this.id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getGallery() {
        return gallery;
    }

    public void setGallery(List<String> gallery) {
        this.gallery = gallery;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public int getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(int costPrice) {
        this.costPrice = costPrice;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public List<ItemOptions> getOptions() {
        return options;
    }

    public void setOptions(List<ItemOptions> options) {
        this.options = options;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Item{" +
                "_id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", detail='" + detail + '\'' +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                ", gallery=" + gallery +
                ", onSale=" + onSale +
                ", costPrice=" + costPrice +
                ", inStock=" + inStock +
                ", options=" + options +
                ", quantity=" + quantity +
                '}';
    }
}
