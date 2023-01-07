package org.shaft.administration.catalog.dao;

import org.shaft.administration.catalog.entity.category.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface CategoryDAO {
    Flux<Category> getCategories(int accountId);
    Mono<Category> saveCategory(int accountId, Map<String,Object> category);
}
