package org.shaft.administration.catalog.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.shaft.administration.catalog.dao.CategoryDAO;
import org.shaft.administration.catalog.entity.category.Category;
import org.shaft.administration.catalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class CategoryService implements CategoryDAO {

    private CategoryRepository categoryRepository;

    private ObjectMapper mapper;

    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        mapper = new ObjectMapper();
    }

    /**
     * #TODO Implement Cache here
     * @param accountId
     * @return List
     */
    @Override
    public Flux<Category> getCategories(int accountId) {
        ACCOUNT_ID.set(accountId);
        try {
            return categoryRepository.findAll();
        } catch (Exception ex) {
            System.out.println("Exception "+ ex.getMessage());
            return Flux.empty();
        } finally {
            ACCOUNT_ID.remove();
        }
    }

    @Override
    public Mono<Category> saveCategory(int accountId, Map<String,Object> category) {
        ACCOUNT_ID.set(accountId);
        try {
            Category c = mapper.convertValue(category, new TypeReference<Category>() {});
            return categoryRepository.save(c);
        } catch (NoSuchIndexException ex) {
            // #TODO Throw internal error exception. Handle NoSuchIndexException exception for all services which is usually raised in case of no index present
            System.out.printf(ex.getMessage());
        } catch (Exception ex) {
            System.out.printf(ex.getMessage());
        } finally {
            ACCOUNT_ID.remove();
        }
        return Mono.empty();
    }
}
