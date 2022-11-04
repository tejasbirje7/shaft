package org.shaft.administration.catalog.repositories;

import org.shaft.administration.catalog.entity.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemsRepository extends ElasticsearchRepository<Item,String> {

}
