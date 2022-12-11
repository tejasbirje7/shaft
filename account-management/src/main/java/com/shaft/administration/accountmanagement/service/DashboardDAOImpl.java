package com.shaft.administration.accountmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;

import java.util.HashMap;
import java.util.Map;

public class DashboardDAOImpl {
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }
    ObjectMapper mapper;
    ShaftQueryTranslator queryTranslator;

    public DashboardDAOImpl() {
        this.mapper = new ObjectMapper();
        this.queryTranslator = new ShaftQueryTranslator();
    }

    public void pinToDashboard(int accountId, Map<String,Object> rawQuery) {
        ACCOUNT_ID.set(accountId);
        ObjectNode rawQry = mapper.convertValue(rawQuery,ObjectNode.class);
        ObjectNode elasticQuery = queryTranslator.translateToElasticQuery(rawQry,true);



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
        return new HashMap<>();
    }
}
