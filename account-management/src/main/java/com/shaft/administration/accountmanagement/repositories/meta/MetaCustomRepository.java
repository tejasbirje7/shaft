package com.shaft.administration.accountmanagement.repositories.meta;

import com.shaft.administration.accountmanagement.entity.Meta;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface MetaCustomRepository {
    Mono<Meta> getMetaFields(int account, String[] fields);
    Mono<Long> pinToDashboard(int accountId, String query);
}
