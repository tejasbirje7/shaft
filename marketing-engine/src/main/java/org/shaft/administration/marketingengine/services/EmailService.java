package org.shaft.administration.marketingengine.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.marketingengine.dao.EmailDao;
import org.shaft.administration.marketingengine.repositories.EmailRepository;
import org.shaft.administration.obligatory.campaigns.service.QueryConstructor;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EmailService implements EmailDao {
  private static final String SENDGRID_ACCESS_KEY = "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";
  public final ObjectMapper mapper;
  private final QueryConstructor queryConstructor;
  private final ShaftQueryTranslator queryTranslator;
  private final EmailRepository emailRepository;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

  @Autowired
  public EmailService(EmailRepository emailRepository) {
    this.emailRepository = emailRepository;
    this.queryTranslator = new ShaftQueryTranslator();
    this.queryConstructor = new QueryConstructor();
    this.mapper = new ObjectMapper();
  }

  @Override
  public Mono<ObjectNode> sendCampaign(int accountId, ObjectNode requestObject) {
    /*
    ObjectNode q = (ObjectNode) requestObject.get(CampaignConstants.QUERY);
    ObjectNode elasticQuery = translateRawQuery(q);
    try {
      return fireAnalyticsQuery(accountId,elasticQuery)
        .mapNotNull(esResponse -> {
          log.info(esResponse);
          return null;
        })
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
    }*/
    return null;
  }

  public Mono<String> fireAnalyticsQuery(int accountId, ObjectNode jsonQuery) throws JsonProcessingException {
    String query = mapper.writeValueAsString(jsonQuery);
    log.info("Query : {}",query);
    return emailRepository.getQueryResults(accountId,query);
  }

  private ObjectNode translateRawQuery(ObjectNode rawQuery) {
    return queryTranslator.translateToElasticQuery(rawQuery,false);
  }
}
