package org.shaft.administration.marketingengine.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ElasticRestClient {

  private final WebClient webClient;
  @Value("${spring.elasticsearch.host}")
  private String ELASTIC_HOST;
  @Value("${spring.elasticsearch.port}")
  private String ELASTIC_PORT;

  public ElasticRestClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<String> getQueryResults(int accountId, String query) {
    String ELASTIC_URL = ELASTIC_HOST + ":" + ELASTIC_PORT;
    String url = "http://".concat(ELASTIC_URL).concat("/").concat(String.valueOf(accountId)).concat("_1*/").concat("_search");
    return webClient
      .post()
      .uri(url)
      .header("account",String.valueOf(accountId))
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(query))
      .retrieve()
      .bodyToMono(String.class);
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }
}
