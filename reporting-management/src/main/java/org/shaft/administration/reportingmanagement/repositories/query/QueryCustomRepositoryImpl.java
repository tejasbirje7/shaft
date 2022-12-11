package org.shaft.administration.reportingmanagement.repositories.query;


import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.shaft.administration.reportingmanagement.entity.AggregationQueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class QueryCustomRepositoryImpl implements QueryCustomRepository{

    HttpHeaders httpHeaders;
    private final RestTemplate restTemplate;
    private String ELASTIC_URL;

    @Value("${elasticsearch.host}")
    private String ELASTIC_HOST;

    @Value("${elasticsearch.port}")
    private String ELASTIC_PORT;

    private AggregationQueryResults emptyResults = new AggregationQueryResults();

    @Autowired
    public QueryCustomRepositoryImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type","application/json");
    }

    @Override
    public AggregationQueryResults getQueryResults(int accountId,String query) {
        this.ELASTIC_URL = ELASTIC_HOST + ":" + ELASTIC_PORT; // #TODO Move this part to constructor
        HttpEntity<String> entity = new HttpEntity<>(query,httpHeaders);
        String url = "http://".concat(ELASTIC_URL).concat("/").concat(String.valueOf(accountId)).concat("_16*/").concat("_search");
        try {
            ResponseEntity<AggregationQueryResults> response = restTemplate.exchange(
                    url, HttpMethod.POST,entity,AggregationQueryResults.class);
            return response.getBody();
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return emptyResults;
    }
}
