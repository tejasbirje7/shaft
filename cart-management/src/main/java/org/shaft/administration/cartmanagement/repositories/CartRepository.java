package org.shaft.administration.cartmanagement.repositories;

import org.shaft.administration.cartmanagement.entity.Cart;
import org.shaft.administration.cartmanagement.repositories.custom.CartCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends ReactiveCrudRepository<Cart,Object>, CartCustomRepository {
    Flux<Cart> findByI(Integer i);
    Mono<Long> countByI(int i);
}
