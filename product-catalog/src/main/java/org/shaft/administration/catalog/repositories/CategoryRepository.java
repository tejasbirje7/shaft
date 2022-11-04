package org.shaft.administration.catalog.repositories;

import org.shaft.administration.catalog.entity.Category;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryRepository extends ElasticsearchRepository<Category,String> {
}
