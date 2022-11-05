package org.shaft.administration.catalog.entity.item;

public class ItemOptions {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ItemOptions{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
