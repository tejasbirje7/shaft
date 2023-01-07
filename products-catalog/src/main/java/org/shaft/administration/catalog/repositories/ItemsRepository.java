package org.shaft.administration.catalog.repositories;

import org.shaft.administration.catalog.entity.item.Item;
import org.shaft.administration.catalog.repositories.items.ItemsCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface ItemsRepository extends ReactiveCrudRepository<Item,String>, ItemsCustomRepository {
    Flux<Item> findByIdIn(List<String> itemIds);
}

