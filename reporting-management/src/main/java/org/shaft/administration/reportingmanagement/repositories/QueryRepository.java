package org.shaft.administration.reportingmanagement.repositories;

import org.shaft.administration.reportingmanagement.entity.UserEvents;
import org.shaft.administration.reportingmanagement.repositories.query.QueryCustomRepository;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryRepository extends ReactiveElasticsearchRepository<UserEvents,Object>, QueryCustomRepository {
}
