package org.shaft.administration.reportingmanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.shaft.administration.reportingmanagement.constants.ReportingConstants;
import org.shaft.administration.reportingmanagement.constants.ReportingLogs;
import org.shaft.administration.reportingmanagement.dao.SegmentDAO;
import org.shaft.administration.reportingmanagement.entity.Segment;
import org.shaft.administration.reportingmanagement.repositories.QueryRepository;
import org.shaft.administration.reportingmanagement.repositories.SegmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

@Service
@Slf4j
public class SegmentService implements SegmentDAO {
  ObjectMapper mapper;
  ShaftQueryTranslator queryTranslator;
  SegmentRepository segmentRepository;
  QueryRepository queryRepository;

  @Autowired
  public SegmentService(SegmentRepository segmentRepository, QueryRepository queryRepository) {
    this.mapper = new ObjectMapper();
    this.queryTranslator = new ShaftQueryTranslator();
    this.segmentRepository = segmentRepository;
    this.queryRepository = queryRepository;
  }

  @Override
  public Mono<ObjectNode> getSavedSegments(int accountId,Map<String,Object> body) {
    String[] fields;
    if (body!=null && body.containsKey(ReportingConstants.FIELDS)) {
      fields = ((ArrayList<String>) body.get(ReportingConstants.FIELDS)).toArray(new String[0]);
    } else {
      fields = new String[0];
    }
    return segmentRepository.getSegments(fields)
      .collectList()
      .map(i -> ShaftResponseBuilder.buildResponse(
        ShaftResponseCode.SEGMENTS_FETCHED_SUCCESSFULLY,mapper.valueToTree(i)))
      .onErrorResume(error -> {
        log.error(ReportingLogs.EXCEPTION_FETCHING_SEGMENT,error);
        return Mono.just(ShaftResponseBuilder.buildResponse(
          ShaftResponseCode.EXCEPTION_FETCHING_SEGMENTS));
      });
  }

  @Override
  public Mono<ObjectNode> saveSegment(int accountId, Map<String,Object> rawQuery) {
    if (rawQuery.containsKey(ReportingConstants.QUERY) && rawQuery.containsKey(ReportingConstants.NAME)) {
      ObjectNode elasticQuery = translateRawQuery(rawQuery);
      String encodedQuery;

      try {
        String q = mapper.writeValueAsString(elasticQuery);
        byte[] bytesEncoded = Base64.encodeBase64(q.getBytes());
        encodedQuery = new String(bytesEncoded);
      } catch (JsonProcessingException e) {
        log.error(ReportingLogs.SEGMENT_JSON_PARSING_EXCEPTION,rawQuery,accountId);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.JSON_EXCEPTION_PARSING_SAVE_SEGMENT));
      }

      Segment segment = new Segment((int) (System.currentTimeMillis()/1000),
        (String) rawQuery.get(ReportingConstants.NAME),encodedQuery);

      return segmentRepository.save(segment)
        .map(i -> ShaftResponseBuilder.buildResponse(ShaftResponseCode.SEGMENT_SAVED_SUCCESSFULLY,
          mapper.convertValue(i, ObjectNode.class)))
        .onErrorResume(error -> {
          log.error(ReportingLogs.EXCEPTION_SAVING_SEGMENT,error,accountId);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_SAVING_SEGMENT));
        });

    } else {
      log.error(ReportingLogs.BAD_SAVE_SEGMENT_REQUEST,rawQuery,accountId);
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SAVE_SEGMENT_BAD_REQUEST));
    }
  }

  private ObjectNode translateRawQuery(Map<String,Object> rawQuery) {
    ObjectNode rawQry = mapper.convertValue(rawQuery.get(ReportingConstants.QUERY),ObjectNode.class);
    return queryTranslator.translateToElasticQuery(rawQry,true);
  }
}
