package org.shaft.administration.reportingmanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.shaft.administration.reportingmanagement.dao.SegmentDAO;
import org.shaft.administration.reportingmanagement.entity.Segment;
import org.shaft.administration.reportingmanagement.repositories.QueryRepository;
import org.shaft.administration.reportingmanagement.repositories.SegmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }

  @Autowired
  public SegmentService(SegmentRepository segmentRepository, QueryRepository queryRepository) {
    this.mapper = new ObjectMapper();
    this.queryTranslator = new ShaftQueryTranslator();
    this.segmentRepository = segmentRepository;
    this.queryRepository = queryRepository;
  }

  @Override
  public Mono<ObjectNode> getSavedSegments(int accountId,Map<String,Object> body) {
    ACCOUNT_ID.set(accountId);
    Flux<Segment> segmentFlux;
    String[] fields;
    if (body!=null && body.containsKey("fields")) {
      fields = ((ArrayList<String>) body.get("fields")).toArray(new String[0]);
    } else {
      fields = new String[0];
    }
    segmentFlux = segmentRepository.getSegments(fields);
    return segmentFlux
      .collectList()
      .map(i -> {
        ACCOUNT_ID.remove();
        return ShaftResponseBuilder.buildResponse("",mapper.valueToTree(i));
      })
      .onErrorResume(error -> {
        ACCOUNT_ID.remove();
        log.error("{}",error);
        return Mono.just(ShaftResponseBuilder.buildResponse(""));
      });
  }

  @Override
  public Mono<ObjectNode> saveSegment(int accountId, Map<String,Object> rawQuery) {
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
      Segment segment = new Segment((int) (System.currentTimeMillis()/1000),(String) rawQuery.get("name"),encodedQuery);
      return segmentRepository.save(segment)
        .map(i -> {
          ACCOUNT_ID.remove();
          return ShaftResponseBuilder.buildResponse("", mapper.convertValue(i, ObjectNode.class));
        })
        .onErrorResume(error -> {
          ACCOUNT_ID.remove();
          log.error("{}",error);
          return Mono.just(ShaftResponseBuilder.buildResponse(""));
        });
    } else {
      // #TODO Throw BAD_REQUEST exception
      ACCOUNT_ID.remove();
    }
    return Mono.just(ShaftResponseBuilder.buildResponse(""));
  }

  private ObjectNode translateRawQuery(Map<String,Object> rawQuery) {
    ObjectNode rawQry = mapper.convertValue(rawQuery.get("q"),ObjectNode.class);
    return queryTranslator.translateToElasticQuery(rawQry,true);
  }
}
