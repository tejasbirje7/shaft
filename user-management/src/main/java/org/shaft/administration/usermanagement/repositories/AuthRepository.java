package org.shaft.administration.usermanagement.repositories;

import org.shaft.administration.usermanagement.entity.User;
import org.shaft.administration.usermanagement.repositories.fingerprint.AuthCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AuthRepository extends ReactiveCrudRepository<User,String>, AuthCustomRepository {
    Flux<User> findByEAndP(String e, String p);
    Flux<Long> countByE(String e);
}
