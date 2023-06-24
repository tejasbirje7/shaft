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

    @Override
    public List<Segment> getSavedSegments(int accountId) {
        ACCOUNT_ID.set(accountId);
        try {
            //return Lists.newArrayList(segmentRepository.findAll());
            return null;
        } catch (Exception ex) {
            // #TODO return exception
            return new ArrayList<>();
        } finally {
            ACCOUNT_ID.remove();
        }
    }

    @Override
    public boolean saveSegment(int accountId, Map<String,Object> rawQuery) {
        ACCOUNT_ID.set(accountId);
        if (rawQuery.containsKey("q") && rawQuery.containsKey("name")) {
            ObjectNode elasticQuery = translateRawQuery(rawQuery);
            String encodedQuery;
            try {
                String q = mapper.writeValueAsString(elasticQuery);
                byte[] bytesEncoded = Base64.encodeBase64(q.getBytes());
                encodedQuery = new String(bytesEncoded);
            } catch (JsonProcessingException e) {
                ACCOUNT_ID.remove();
                throw new RuntimeException(e);
            }
            Segment s = new Segment((int) (System.currentTimeMillis()/1000),(String) rawQuery.get("name"),encodedQuery);
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
        ACCOUNT_ID.remove();
        return false;
    }

    private ObjectNode translateRawQuery(Map<String,Object> rawQuery) {
        ObjectNode rawQry = mapper.convertValue(rawQuery.get("q"),ObjectNode.class);
        return queryTranslator.translateToElasticQuery(rawQry,true);
    }
}
