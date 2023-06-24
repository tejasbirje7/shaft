package org.shaft.administration.reportingmanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.shaft.administration.reportingmanagement.dao.QueryDao;
import org.shaft.administration.reportingmanagement.entity.AggregationQueryResults;
import org.shaft.administration.reportingmanagement.repositories.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class QueryDAOImpl implements QueryDao {
    ObjectMapper mapper;
    ShaftQueryTranslator queryTranslator;
    QueryRepository queryRepository;
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public QueryDAOImpl(QueryRepository queryRepository) {
        this.mapper = new ObjectMapper();
        this.queryTranslator = new ShaftQueryTranslator();
        this.queryRepository = queryRepository;
    }

    @Override
    public Mono<ObjectNode> evaluateEncodedQueries(int accountId, Map<String,Object> request) {
        ACCOUNT_ID.set(accountId);
        if(request.containsKey("q")) {
            String q = (String) request.get("q");
            byte[] decoded = Base64.decodeBase64(q);
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);
            ObjectNode convertedQuery = mapper.convertValue(decodedStr,ObjectNode.class);
            ObjectNode elasticQuery = queryTranslator.translateToElasticQuery(convertedQuery,true);
            // #TODO Hit to elasticsearch and get results
            try {
                return fireAnalyticsQuery(accountId,elasticQuery);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            } finally {
                ACCOUNT_ID.remove();
            }
        } else {
            ACCOUNT_ID.remove();
            // #TODO Throw Bad request exception
        }
        return Mono.just(ShaftResponseBuilder.buildResponse(""));
    }

    @Override
    public Mono<ObjectNode> getQueryResults(int accountId, Map<String, Object> rawQuery) {
        ACCOUNT_ID.set(accountId);
        if(rawQuery.containsKey("q")) {
            ObjectNode rawQry = mapper.convertValue(rawQuery.get("q"),ObjectNode.class);
            ObjectNode elasticQuery = queryTranslator.translateToElasticQuery(rawQry,true);
            try {
                return fireAnalyticsQuery(accountId,elasticQuery);
            } catch (JsonProcessingException e) {
                //throw new RuntimeException(e);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return Mono.just(ShaftResponseBuilder.buildResponse(""));
            } finally {
                ACCOUNT_ID.remove();
            }
        } else {
            ACCOUNT_ID.remove();
            //throw new RuntimeException("Query not present in request");
            return Mono.just(ShaftResponseBuilder.buildResponse(""));
        }
        return Mono.just(ShaftResponseBuilder.buildResponse(""));
    }

    public Mono<ObjectNode> fireAnalyticsQuery(int accountId, ObjectNode jsonQuery) throws JsonProcessingException {
        String query = mapper.writeValueAsString(jsonQuery);
        log.info("Query : {}",query);
        return queryRepository.getQueryResults(accountId,query).map(queryResponse -> {
            log.info("Query Response : {}",queryResponse);
            if (queryResponse.getAggregations() != null) {
                Map<String,Object> response = new HashMap<>();
                response.put("u",queryResponse.getUserCount());
                response.put("g",queryResponse.getGraphCount());
                return ShaftResponseBuilder.buildResponse("",mapper.valueToTree(response));
            } else {
                // #TODO return query failed exception
                //throw new RuntimeException("Query Failed");
                return ShaftResponseBuilder.buildResponse("");
            }
        });
    }
}
