package org.shaft.administration.catalog.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.shaft.administration.catalog.dao.CategoryDAO;
import org.shaft.administration.catalog.entity.category.Category;
import org.shaft.administration.catalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CategoryDAOImpl implements CategoryDAO {

    private CategoryRepository categoryRepository;

    private ObjectMapper mapper = new ObjectMapper();

    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * #TODO Implement Cache here
     * @param accountId
     * @return List
     */
    @Override
    public List<Category> getCategories(int accountId) {
        ACCOUNT_ID.set(accountId);
        try {
            return Lists.newArrayList(categoryRepository.findAll());
        } catch (Exception ex) {
            System.out.println("Exception "+ ex.getMessage());
            return new ArrayList<>();
        } finally {
            ACCOUNT_ID.remove();
        }
    }

    @Override
    public Category saveCategory(int accountId, Map<String,Object> category) {
        ACCOUNT_ID.set(accountId);
        Category c = new Category();
        try {
            c = mapper.convertValue(category, new TypeReference<Category>() {});
            categoryRepository.save(c);
        } catch (NoSuchIndexException ex) {
            // #TODO Throw internal error exception. Handle NoSuchIndexException exception for all services which is usually raised in case of no index present
            System.out.printf(ex.getMessage());
        } catch (Exception ex) {
            System.out.printf(ex.getMessage());
        } finally {
            ACCOUNT_ID.remove();
        }
        return c;
    }
}
