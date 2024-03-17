package org.shaft.administration.accountmanagement.repositories.meta;

import org.shaft.administration.accountmanagement.entity.EventsMeta;
import org.shaft.administration.accountmanagement.entity.Meta;
import org.shaft.administration.accountmanagement.entity.PropsMeta;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MetaCustomRepository {
    Mono<Meta> getMetaFields(int account, String[] fields);
    Mono<Meta> getMetaFields(int account);
    Mono<Long> pinToDashboard(int accountId, String query);
    Flux<EventsMeta> getEventsMeta(int accountId);
    Flux<PropsMeta> getPropsMeta(int accountId);
    Mono<Meta> saveAccountMeta(Meta accountInfo);
}
