package org.shaft.administration.reportingmanagement.repositories.query;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.reportingmanagement.entity.AggregationQueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Repository
public class QueryCustomRepositoryImpl implements QueryCustomRepository{

    HttpHeaders httpHeaders;
    private final RestTemplate restTemplate;
    @Value("${elasticsearch.host}")
    private String ELASTIC_HOST;
    @Value("${elasticsearch.port}")
    private String ELASTIC_PORT;
    private final AggregationQueryResults emptyResults = new AggregationQueryResults();
    private final ObjectMapper mapper;

    @Autowired
    public QueryCustomRepositoryImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        httpHeaders = new HttpHeaders();
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public AggregationQueryResults getQueryResults(int accountId, String query) {
        String ELASTIC_URL = ELASTIC_HOST + ":" + ELASTIC_PORT; // #TODO Move this part to constructor
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(query,httpHeaders);
        String url = "http://".concat(ELASTIC_URL).concat("/").concat(String.valueOf(accountId)).concat("_16*/").concat("_search");
        try {
            ResponseEntity<ObjectNode> response = restTemplate.exchange(
                    url, HttpMethod.POST,entity, ObjectNode.class);
            return mapper.convertValue(response.getBody(),AggregationQueryResults.class);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return emptyResults;
    }
}
