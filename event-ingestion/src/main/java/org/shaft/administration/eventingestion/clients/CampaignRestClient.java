package org.shaft.administration.eventingestion.clients;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CampaignRestClient {
  private final WebClient webClient;
  @Value("${shaft.services.campaign-url}")
  private static String CAMPAIGN_HOST;
  private static final String CAMPAIGN_QUALIFICATION_URL = "http://"+CAMPAIGN_HOST+":8080/marketing/campaign/qualification";

  @Autowired
  public CampaignRestClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<String> checkIfCampaignExists(int accountId, JsonNode requestPayload) {
    return webClient
      .post()
      .uri(CAMPAIGN_QUALIFICATION_URL)
      .header("account", String.valueOf(accountId))
      .body(BodyInserters.fromValue(requestPayload))
      .retrieve()
      .bodyToMono(String.class);
    //.retryWhen(RetryUtil.retrySpec())
  }
}
