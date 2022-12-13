package com.shaft.administration.accountmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shaft.administration.accountmanagement.dao.DashboardDAO;
import com.shaft.administration.accountmanagement.entity.Meta;
import com.shaft.administration.accountmanagement.repositories.MetaRepository;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardDAOImpl implements DashboardDAO {

    MetaRepository metaRepository;
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }
    ObjectMapper mapper;
    ShaftQueryTranslator queryTranslator;

    @Autowired
    public DashboardDAOImpl(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
        this.mapper = new ObjectMapper();
        this.queryTranslator = new ShaftQueryTranslator();
    }

    @Override
    public boolean pinToDashboard(int accountId, Map<String,Object> rawQuery) {
        ACCOUNT_ID.set(accountId);
        Meta meta = metaRepository.getMetaFields(accountId,new String[]{"dashboardQueries"});
        if (meta.getDashboardQueries().size() > 5) {
            // Introduced limit here since on app launch app should not wait to load more query results
            // #TODO Throw limit exceeded exception
            throw new RuntimeException("Limit Exceeded for query");
        } else {
            ObjectNode rawQry = mapper.convertValue(rawQuery,ObjectNode.class);
            ObjectNode query = this.queryTranslator.translateToElasticQuery(rawQry,true);
            try {
                String q = mapper.writeValueAsString(query);
                byte[] bytesEncoded = Base64.encodeBase64(q.getBytes());
                Long recordUpdatedCount = metaRepository.pinToDashboard(accountId,new String(bytesEncoded));
                return recordUpdatedCount > 0;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (Exception ex) {
                throw  new RuntimeException(ex.getMessage());
            }
        }
        /*
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
        return new HashMap<>();*/
    }
}
