package org.shaft.administration.catalog.repositories;

import org.shaft.administration.catalog.entity.item.Item;
import org.shaft.administration.catalog.repositories.items.ItemsCustomRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemsRepository extends ElasticsearchRepository<Item,String>, ItemsCustomRepository {
    public List<Item> findByIdIn(List<String> itemIds);
}
