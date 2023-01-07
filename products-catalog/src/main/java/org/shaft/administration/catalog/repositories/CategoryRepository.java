package org.shaft.administration.catalog.repositories;

import org.shaft.administration.catalog.entity.category.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ReactiveCrudRepository<Category,String> {
}
