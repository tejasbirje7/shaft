package org.shaft.administration.catalog.repositories;

import org.shaft.administration.catalog.entity.category.Category;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ElasticsearchRepository<Category,String> {
}
