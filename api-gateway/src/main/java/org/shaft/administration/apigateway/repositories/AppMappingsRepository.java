package org.shaft.administration.apigateway.repositories;

import org.shaft.administration.apigateway.entity.AppMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppMappingsRepository extends ElasticsearchRepository<AppMapping,String> {

}
