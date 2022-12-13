package org.shaft.administration.reportingmanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.shaft.administration.reportingmanagement.dao.QueryDao;
import org.shaft.administration.reportingmanagement.entity.AggregationQueryResults;
import org.shaft.administration.reportingmanagement.repositories.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
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
    public Map<String, Object> getQueryResults(int accountId, Map<String, Object> rawQuery) {
        ACCOUNT_ID.set(accountId);
        if(rawQuery.containsKey("q")) {
            ObjectNode elasticQuery = getQueryFromRawObject(rawQuery);
            try {
                String query = mapper.writeValueAsString(elasticQuery);
                AggregationQueryResults results = queryRepository.getQueryResults(accountId,query);
                // #TODO Construct proper response
                System.out.println(results);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            } finally {
                ACCOUNT_ID.remove();
            }
        } else {
            throw new RuntimeException("Query not present in request");
        }
        return new HashMap<>();
    }

    private ObjectNode getQueryFromRawObject(Map<String,Object> rawQuery) {
        ObjectNode rawQry = mapper.convertValue(rawQuery.get("q"),ObjectNode.class);
        return queryTranslator.translateToElasticQuery(rawQry,true);
    }
}
