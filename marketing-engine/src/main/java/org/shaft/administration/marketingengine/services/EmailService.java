package org.shaft.administration.marketingengine.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.shaft.administration.marketingengine.clients.SendGridRestClient;
import org.shaft.administration.marketingengine.constants.CampaignConstants;
import org.shaft.administration.marketingengine.dao.EmailDao;
import org.shaft.administration.marketingengine.entity.CampaignCriteria.CampaignCriteria;
import org.shaft.administration.marketingengine.repositories.CampaignRepository;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class EmailService implements EmailDao {
  private static final String SENDGRID_ACCESS_KEY = "Bearer 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";
  public final ObjectMapper mapper;
  public final ObjectReader objectNodeParser;
  private final ShaftQueryTranslator queryTranslator;
  private final CampaignRepository campaignRepository;
  private final SendGridRestClient sendGridRestClient;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

  @Autowired
  public EmailService(CampaignRepository campaignRepository, SendGridRestClient sendGridRestClient) {
    this.campaignRepository = campaignRepository;
    this.sendGridRestClient = sendGridRestClient;
    this.queryTranslator = new ShaftQueryTranslator();
    this.mapper = new ObjectMapper();
    this.objectNodeParser = new ObjectMapper().readerFor(ObjectNode.class);
  }

  @Override
  public Mono<ObjectNode> sendCampaign(int accountId, ObjectNode requestObject) {

    ObjectNode q = (ObjectNode) requestObject.get("q");
    ObjectNode elasticQuery = translateRawQuery(q);
    try {
      return fireAnalyticsQuery(accountId,elasticQuery)
        .mapNotNull(esResponse -> {

          /* #SEND EMAIL API

          curl --location --request POST 'https://api.sendgrid.com/v3/mail/send' \
--header 'Authorization: Bearer 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' \
--header 'Content-Type: application/json' \
--data-raw '{
    "personalizations": [
        {
            "to": [
                {
                    "email": "birjetejas2022@gmail.com"
                },
                {
                    "email": "tejas.birje7@gmail.com"
                }
            ]
        }
    ],
    "from": {
        "email": "tejushaft@gmail.com"
    },
    "subject": "Sending with SendGrid is Fun",
    "content": [
        {
            "type": "text/plain",
            "value": "As generative artificial intelligence (AI) has become ubiquitous"
        }
    ]
}'


           */
          log.info(esResponse);
          return ShaftResponseBuilder.buildResponse("S");
        })
        .onErrorResume(error -> {
//          log.error(ReportingLogs.FAILED_PROCESSING_QUERY,error);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_PROCESSING_QUERY));
        });
    } catch (JsonProcessingException e) {
//      log.error(ReportingLogs.JSON_PARSING_FAILED,rawQuery,accountId);
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.JSON_PARSING_FAILED));
    } catch (Exception ex) {
//      log.error(ReportingLogs.PROCESSING_QUERY_EXCEPTION,rawQry,accountId);
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_PROCESSING_QUERY));
    }
  }

  public void getStats() {
    // #TODO Stats API

          /* #STATS API
          curl --location --request GET 'https://api.sendgrid.com/v3/stats?start_date=2024-03-01' \
--header 'Authorization: Bearer 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' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'start_date=2024-03-01'
           */

  }


  public Mono<String> fireAnalyticsQuery(int accountId, ObjectNode jsonQuery) throws JsonProcessingException {
    String query = mapper.writeValueAsString(jsonQuery);
    log.info("Query : {}",query);
    //return campaignRepository.getPaginatedQueryResults(accountId,query,0);
    return  Mono.empty();
  }

  private ObjectNode translateRawQuery(ObjectNode rawQuery) {
    return queryTranslator.translateToElasticQuery(rawQuery,false);
  }

}
