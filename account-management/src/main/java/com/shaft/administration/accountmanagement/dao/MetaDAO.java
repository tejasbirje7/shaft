package com.shaft.administration.accountmanagement.dao;

import com.shaft.administration.accountmanagement.entity.Meta;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface MetaDAO {
    Mono<Meta> getMetaFields(int account, Map<String,Object> fields);
}
