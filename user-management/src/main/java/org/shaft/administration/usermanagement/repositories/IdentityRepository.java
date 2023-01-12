package org.shaft.administration.usermanagement.repositories;

import org.shaft.administration.usermanagement.entity.Identity;
import org.shaft.administration.usermanagement.repositories.fingerprint.IdentityCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityRepository extends ReactiveCrudRepository<Identity,String>, IdentityCustomRepository {

}
