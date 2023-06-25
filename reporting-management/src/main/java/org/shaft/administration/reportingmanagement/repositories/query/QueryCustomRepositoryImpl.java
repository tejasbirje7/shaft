package org.shaft.administration.reportingmanagement.repositories.query;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.shaft.administration.reportingmanagement.clients.RestClient;
import org.shaft.administration.reportingmanagement.entity.AggregationQueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class QueryCustomRepositoryImpl implements QueryCustomRepository{

    private final AggregationQueryResults emptyResults = new AggregationQueryResults();
    private final ObjectMapper mapper;
    private final RestClient restClient;

    @Autowired
    public QueryCustomRepositoryImpl(RestClient restClient) {
        this.restClient = restClient;
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public Mono<AggregationQueryResults> getQueryResults(int accountId, String query) {
        try {
            return restClient.getQueryResults(accountId,query);
        } catch (Exception ex){
            throw new RuntimeException("Error fetching query results",ex);
        }
    }
}
