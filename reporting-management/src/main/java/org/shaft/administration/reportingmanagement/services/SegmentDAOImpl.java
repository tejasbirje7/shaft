package org.shaft.administration.reportingmanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.shaft.administration.reportingmanagement.dao.SegmentDAO;
import org.shaft.administration.reportingmanagement.entity.Segment;
import org.shaft.administration.reportingmanagement.repositories.QueryRepository;
import org.shaft.administration.reportingmanagement.repositories.SegmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SegmentDAOImpl implements SegmentDAO {
    ObjectMapper mapper;
    ShaftQueryTranslator queryTranslator;
    SegmentRepository segmentRepository;
    QueryRepository queryRepository;
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public SegmentDAOImpl(SegmentRepository segmentRepository, QueryRepository queryRepository) {
        this.mapper = new ObjectMapper();
        this.queryTranslator = new ShaftQueryTranslator();
        this.segmentRepository = segmentRepository;
        this.queryRepository = queryRepository;
    }

    public void evaluateEncodedQueries(int accountId,Map<String,Object> request) {
        ACCOUNT_ID.set(accountId);
        if(request.containsKey("q")) {
            String q = (String) request.get("q");
            byte[] decoded = Base64.decodeBase64(q);
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);
            ObjectNode convertedQuery = mapper.convertValue(decodedStr,ObjectNode.class);
            ObjectNode elasticQuery = queryTranslator.translateToElasticQuery(convertedQuery,true);
            // #TODO Hit to elasticsearch and get results

        } else {
            ACCOUNT_ID.remove();
            // #TODO Throw Bad request exception
        }

    }

    @Override
    public List<Segment> getSavedFilters(int accountId) {
        ACCOUNT_ID.set(accountId);
        try {
            return Lists.newArrayList(segmentRepository.findAll());
        } catch (Exception ex) {
            // #TODO return exception
            return new ArrayList<>();
        } finally {
            ACCOUNT_ID.remove();
        }
    }

    @Override
    public boolean saveFilters(int accountId, Map<String,Object> rawQuery) {
        ACCOUNT_ID.set(accountId);
        if (rawQuery.containsKey("q") && rawQuery.containsKey("name")) {
            ObjectNode elasticQuery = getQueryFromRawObject(rawQuery);
            String q = null;
            try {
                q = mapper.writeValueAsString(elasticQuery);
                byte[] bytesEncoded = Base64.encodeBase64(q.getBytes());
            } catch (JsonProcessingException e) {
                ACCOUNT_ID.remove();
                throw new RuntimeException(e);
            }
            Segment s = new Segment();
            s.setNm((String) rawQuery.get("name"));
            s.setQ(q);
            s.setSid((int) (System.currentTimeMillis()/1000));
            try {
                segmentRepository.save(s);
                return true;
            } catch (Exception ex) {
                ACCOUNT_ID.remove();
                // #TODO Throw BAD_REQUEST exception
            }
        } else {
            // #TODO Throw BAD_REQUEST exception
            ACCOUNT_ID.remove();
        }
        return false;
    }

    private ObjectNode getQueryFromRawObject(Map<String,Object> rawQuery) {
        ObjectNode rawQry = mapper.convertValue(rawQuery.get("q"),ObjectNode.class);
        return queryTranslator.translateToElasticQuery(rawQry,true);
    }
}
