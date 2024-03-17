package org.shaft.administration.marketingengine.clients;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class SendGridRestClient {

  private final WebClient webClient;
  private static final String SENDGRID_URL_TO_SEND_EMAIL = "https://api.sendgrid.com/v3/mail/send";

  public SendGridRestClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<String> dispatchEmail(String payload, String accessKey) {
    return webClient
      .post()
      .uri(SENDGRID_URL_TO_SEND_EMAIL)
      .header("Authorization","Bearer " + accessKey )
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(payload))
      .retrieve()
      .bodyToMono(String.class);
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }
}
