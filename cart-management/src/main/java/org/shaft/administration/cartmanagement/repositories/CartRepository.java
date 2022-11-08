package org.shaft.administration.cartmanagement.repositories;

import org.shaft.administration.cartmanagement.entity.Cart;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CartRepository extends ElasticsearchRepository<Cart,String> {
    public boolean deleteByI(int i);
}
