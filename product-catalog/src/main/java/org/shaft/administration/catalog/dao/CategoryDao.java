package org.shaft.administration.catalog.dao;

import org.shaft.administration.catalog.entity.Category;

import java.util.List;

public interface CategoryDao {
    public List<Category> getCategories(int accountId);
}
