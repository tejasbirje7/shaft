package org.shaft.administration.catalog.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.catalog.entity.category.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface CategoryDAO {
    Mono<ObjectNode> getCategories(int accountId);
    Mono<ObjectNode> saveCategory(int accountId, Map<String,Object> category);
}
