package org.shaft.administration.cartmanagement.repositories;

import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.repositories.custom.CartCustomRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CartRepository extends ElasticsearchRepository<Cart,Object>, CartCustomRepository {
    public void deleteCartBy(Integer i);
    public Cart findByI(Integer i);
}
