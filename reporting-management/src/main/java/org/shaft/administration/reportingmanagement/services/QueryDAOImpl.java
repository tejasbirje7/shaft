package org.shaft.administration.reportingmanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.shaft.administration.reportingmanagement.dao.QueryDao;
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
        ObjectNode rawQry = mapper.convertValue(rawQuery,ObjectNode.class);
        ObjectNode elasticQuery = queryTranslator.translateToElasticQuery(rawQry,true);
        try {
            String query = mapper.writeValueAsString(elasticQuery);
            Object document = queryRepository.getQueryResults(query);
            System.out.println(document);
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
