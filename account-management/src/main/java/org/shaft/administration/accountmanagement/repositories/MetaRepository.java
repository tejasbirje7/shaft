package org.shaft.administration.accountmanagement.repositories;

import org.shaft.administration.accountmanagement.entity.Meta;
import org.shaft.administration.accountmanagement.repositories.meta.MetaCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaRepository extends ReactiveCrudRepository<Meta,String>, MetaCustomRepository {
}
