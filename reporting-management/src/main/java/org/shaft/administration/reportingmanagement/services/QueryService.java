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
import org.shaft.administration.reportingmanagement.dao.QueryDao;
import org.shaft.administration.reportingmanagement.entity.AggregationQueryResults;
import org.shaft.administration.reportingmanagement.repositories.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class QueryService implements QueryDao {
  ObjectMapper mapper;
  ShaftQueryTranslator queryTranslator;
  QueryRepository queryRepository;

  @Autowired
  public QueryService(QueryRepository queryRepository) {
    this.mapper = new ObjectMapper();
    this.queryTranslator = new ShaftQueryTranslator();
    this.queryRepository = queryRepository;
  }

  @Override
  public Mono<ObjectNode> evaluateEncodedQueries(int accountId, Map<String,Object> request) {
    if(request.containsKey(ReportingConstants.QUERY)) {
      String q = (String) request.get(ReportingConstants.QUERY);
      byte[] decoded = Base64.decodeBase64(q);
      String decodedStr = new String(decoded, StandardCharsets.UTF_8);
      ObjectNode convertedQuery = mapper.convertValue(decodedStr,ObjectNode.class);
      ObjectNode elasticQuery = queryTranslator.translateToElasticQuery(convertedQuery,true);
      try {
        return fireAnalyticsQuery(accountId,elasticQuery)
          .map(response -> ShaftResponseBuilder.buildResponse(ShaftResponseCode.EVALUATED_ENCODED_QUERIES,constructQuery(response)))
          .onErrorResume(error -> {
            log.error(ReportingLogs.ENCODED_QUERY_EVALUATION_EXCEPTION,error);
            return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_EVALUATING_ENCODED_QUERY));
          });
      } catch (JsonProcessingException e) {
        log.error(ReportingLogs.JSON_PARSING_FAILED,request,accountId);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.JSON_PARSING_FAILED));
      } catch (Exception ex) {
        log.error(ReportingLogs.ENCODED_QUERY_EVALUATION_FAILED,q,accountId);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_EVALUATING_ENCODED_QUERY));
      }
    } else {
      log.error(ReportingLogs.BAD_QUERY_REQUEST,accountId);
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BAD_REQUEST_FOR_ENCODED_QUERIES));
    }
  }

  @Override
  public Mono<ObjectNode> getQueryResults(int accountId, Map<String, Object> rawQuery) {
    if(rawQuery.containsKey(ReportingConstants.QUERY)) {
      ObjectNode rawQry = mapper.convertValue(rawQuery.get(ReportingConstants.QUERY),ObjectNode.class);
      ObjectNode elasticQuery = queryTranslator.translateToElasticQuery(rawQry,true);
      try {
        return fireAnalyticsQuery(accountId,elasticQuery)
          .map(response -> ShaftResponseBuilder.buildResponse(ShaftResponseCode.QUERY_RESULTS_FETCHED,constructQuery(response)))
          .onErrorResume(error -> {
            log.error(ReportingLogs.FAILED_PROCESSING_QUERY,error);
            return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_PROCESSING_QUERY));
          });
      } catch (JsonProcessingException e) {
        log.error(ReportingLogs.JSON_PARSING_FAILED,rawQuery,accountId);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.JSON_PARSING_FAILED));
      } catch (Exception ex) {
        log.error(ReportingLogs.PROCESSING_QUERY_EXCEPTION,rawQry,accountId);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_PROCESSING_QUERY));
      }
    } else {
      log.error(ReportingLogs.BAD_QUERY,rawQuery,accountId);
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BAD_QUERY_REQUEST));
    }
  }

  public Mono<AggregationQueryResults> fireAnalyticsQuery(int accountId, ObjectNode jsonQuery) throws JsonProcessingException {
    String query = mapper.writeValueAsString(jsonQuery);
    log.info("Query : {}",query);
    return queryRepository.getQueryResults(accountId,query);
  }

  public ObjectNode constructQuery(AggregationQueryResults queryResponse) {
    ObjectNode response = mapper.createObjectNode();
    response.put(ReportingConstants.USER_COUNT,queryResponse.getUserCount());
    response.set(ReportingConstants.GRAPH_COUNT,queryResponse.getGraphCount());
    if (queryResponse.getAggregations() != null) {
      return ShaftResponseBuilder.buildResponse(ShaftResponseCode.QUERY_RESULTS_PROCESSED_SUCCESSFULLY,response);
    } else {
      return ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_PROCESSING_QUERY_RESULTS);
    }
  }
}
