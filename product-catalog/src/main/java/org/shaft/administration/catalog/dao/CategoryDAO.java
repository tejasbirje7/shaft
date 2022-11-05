package org.shaft.administration.catalog.dao;

import org.shaft.administration.catalog.entity.category.Category;

import java.util.List;

public interface CategoryDAO {
    public List<Category> getCategories(int accountId);
}
