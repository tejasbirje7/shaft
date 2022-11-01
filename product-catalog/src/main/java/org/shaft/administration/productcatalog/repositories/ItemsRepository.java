package org.shaft.administration.productcatalog.repositories;

import org.shaft.administration.productcatalog.entity.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemsRepository extends ElasticsearchRepository<Item,String> {

}
