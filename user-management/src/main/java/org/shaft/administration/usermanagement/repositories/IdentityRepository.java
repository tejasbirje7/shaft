package org.shaft.administration.usermanagement.repositories;

import org.shaft.administration.usermanagement.entity.Identity;
import org.shaft.administration.usermanagement.repositories.fingerprint.IdentityCustomRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IdentityRepository extends ElasticsearchRepository<Identity,String>, IdentityCustomRepository {
}
