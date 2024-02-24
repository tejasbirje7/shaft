package org.shaft.administration.accountmanagement.clients;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class ElasticRestClient {
  private final WebClient webClient;
  private final String ELASTIC_HOST;
  private final String ELASTIC_PORT;
  private final ReactiveElasticsearchClient reactiveElasticsearchClient;

  public ElasticRestClient(WebClient webClient,
                           @Value("${spring.elasticsearch.host}") String elasticHost,
                           @Value("${spring.elasticsearch.port}") String elasticPort, ReactiveElasticsearchClient reactiveElasticsearchClient) {
    this.webClient = webClient;
    this.ELASTIC_HOST = elasticHost;
    this.ELASTIC_PORT = elasticPort;
    this.reactiveElasticsearchClient = reactiveElasticsearchClient;
  }
  public Mono<String> insertDeviceMappings(int accountId, String requestBody) {
    String ELASTIC_URL = "http://" + ELASTIC_HOST + ":" + ELASTIC_PORT + "/" + accountId + "_device_mapping";
    return webClient
      .put()
      .uri(ELASTIC_URL)
      .header("Content-Type","application/json")
      .body(BodyInserters.fromValue(requestBody))
      .retrieve()
      .onStatus(HttpStatus::isError,clientResponse -> Mono.error(new Exception("Exception inserting mappings")))
      .bodyToMono(String.class);
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }

  public Mono<String> insertEventRequestsMapping(String indexName, String requestBody) {
    String ELASTIC_URL = "http://" + ELASTIC_HOST + ":" + ELASTIC_PORT + "/" + indexName;
    return webClient
      .put()
      .uri(ELASTIC_URL)
      .header("Content-Type","application/json")
      .body(BodyInserters.fromValue(requestBody))
      .retrieve()
      .onStatus(HttpStatus::isError,clientResponse -> Mono.error(new Exception("Exception inserting event requests mapping")))
      .bodyToMono(String.class);
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }

  public Mono<String> createTemplateConfigurationIndex(int accountId) {
    String ELASTIC_URL = "http://" + ELASTIC_HOST + ":" + ELASTIC_PORT + "/" + accountId + "_template_configuration";
    return webClient
      .put()
      .uri(ELASTIC_URL)
      .header("Content-Type","application/json")
      .retrieve()
      .onStatus(HttpStatus::isError,clientResponse -> Mono.error(new Exception("Exception inserting event requests mapping")))
      .bodyToMono(String.class);
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }
}
