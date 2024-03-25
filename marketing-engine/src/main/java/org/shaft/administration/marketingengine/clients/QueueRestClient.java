package org.shaft.administration.marketingengine.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class QueueRestClient {

  private final WebClient webClient;
  private final static String SCHEDULED_CAMPAIGN_QUEUE_URL = "http://localhost:9001/campaign/enqueue";
  private final static String ENQUEUE_QUALIFIED_USERS_URL = "http://localhost:9002/users/enqueue";

  public QueueRestClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<ObjectNode> enqueueScheduledCampaign(int account,JsonNode campaign) {
    return webClient
      .put()
      .uri(SCHEDULED_CAMPAIGN_QUEUE_URL)
      .header("Content-Type","application/json")
      .header("account", String.valueOf(account))
      .body(BodyInserters.fromValue(campaign))
      .retrieve()
      .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new Exception(String.format("Exception enqueuing campaign %s",campaign.get("cid").asText()))))
      .bodyToMono(ObjectNode.class)
      .timeout(Duration.ofSeconds(10));
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }

  public Mono<ObjectNode> enqueueQualifiedUsers(int account,JsonNode campaign,int campaignType) {
    return webClient
      .put()
      .uri(ENQUEUE_QUALIFIED_USERS_URL)
      .header("Content-Type","application/json")
      .header("account", String.valueOf(account))
      .header("campaignType", String.valueOf(campaignType))
      .body(BodyInserters.fromValue(campaign))
      .retrieve()
      .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new Exception(String.format("Exception enqueuing qualified users %s",campaign.get("cid").asText()))))
      .bodyToMono(ObjectNode.class)
      .timeout(Duration.ofSeconds(10));
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }


}
