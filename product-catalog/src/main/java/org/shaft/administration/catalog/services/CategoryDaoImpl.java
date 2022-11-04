package org.shaft.administration.catalog.services;

import com.google.common.collect.Lists;
import org.shaft.administration.catalog.dao.CategoryDao;
import org.shaft.administration.catalog.entity.Category;
import org.shaft.administration.catalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryDaoImpl implements CategoryDao {

    private CategoryRepository categoryRepository;

    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategories(int accountId) {
        ACCOUNT_ID.set(accountId);
        try {
            return Lists.newArrayList(categoryRepository.findAll());
        } catch (Exception ex) {
            System.out.println("Exception "+ ex.getMessage());
            return new ArrayList<>();
        }
    }
}
