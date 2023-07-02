package com.shaft.administration.accountmanagement.repositories.meta;

import com.shaft.administration.accountmanagement.entity.EventProps;
import com.shaft.administration.accountmanagement.entity.EventsMeta;
import com.shaft.administration.accountmanagement.entity.Meta;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MetaCustomRepository {
    Mono<Meta> getMetaFields(int account, String[] fields);
    Mono<Long> pinToDashboard(int accountId, String query);
    Flux<EventsMeta> getEventsMeta(int accountId);
}
