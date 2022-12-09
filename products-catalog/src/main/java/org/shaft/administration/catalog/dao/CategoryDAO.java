package org.shaft.administration.catalog.dao;

import org.shaft.administration.catalog.entity.category.Category;

import java.util.List;
import java.util.Map;

public interface CategoryDAO {
    public List<Category> getCategories(int accountId);
    Category saveCategory(int accountId, Map<String,Object> category);
}
