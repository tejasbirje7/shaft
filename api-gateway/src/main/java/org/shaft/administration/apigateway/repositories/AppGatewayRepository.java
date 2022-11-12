package org.shaft.administration.apigateway.repositories;

import org.shaft.administration.apigateway.entity.Mappings.AppMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppGatewayRepository extends ElasticsearchRepository<AppMapping,String> {

}
