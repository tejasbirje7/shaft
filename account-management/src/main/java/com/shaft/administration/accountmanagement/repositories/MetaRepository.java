package com.shaft.administration.accountmanagement.repositories;

import com.shaft.administration.accountmanagement.entity.Meta;
import com.shaft.administration.accountmanagement.repositories.meta.MetaCustomRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaRepository extends ElasticsearchRepository<Meta,String>, MetaCustomRepository {
}
