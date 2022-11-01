package org.shaft.administration.apigateway.repositories;

import org.shaft.administration.apigateway.entity.AppMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AppMappingsRepository extends ElasticsearchRepository<AppMapping,String> {

}
