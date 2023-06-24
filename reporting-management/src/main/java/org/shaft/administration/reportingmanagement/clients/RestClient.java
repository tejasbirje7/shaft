package org.shaft.administration.reportingmanagement.clients;

import org.shaft.administration.reportingmanagement.entity.AggregationQueryResults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RestClient {

  private final WebClient webClient;
  @Value("${spring.elasticsearch.host}")
  private String ELASTIC_HOST;
  @Value("${spring.elasticsearch.port}")
  private String ELASTIC_PORT;

  public RestClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<AggregationQueryResults> getQueryResults(int accountId, String query) {
    String ELASTIC_URL = ELASTIC_HOST + ":" + ELASTIC_PORT;
    String url = "http://".concat(ELASTIC_URL).concat("/").concat(String.valueOf(accountId)).concat("_16*/").concat("_search");
    return webClient
      .post()
      .uri(url)
      .header("account",String.valueOf(accountId))
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(query))
      .retrieve()
      .bodyToMono(AggregationQueryResults.class);
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }
}
