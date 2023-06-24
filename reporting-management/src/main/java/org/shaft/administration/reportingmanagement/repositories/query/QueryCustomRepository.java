package org.shaft.administration.reportingmanagement.repositories.query;


import org.shaft.administration.reportingmanagement.entity.AggregationQueryResults;
import reactor.core.publisher.Mono;

public interface QueryCustomRepository {
    Mono<AggregationQueryResults> getQueryResults(int accountId, String query);
}
