package org.shaft.administration.reportingmanagement.repositories.query;


import org.shaft.administration.reportingmanagement.entity.AggregationQueryResults;

import java.util.Map;

public interface QueryCustomRepository {
    AggregationQueryResults getQueryResults(int accountId, String query);
}
