package org.shaft.administration.eventprocessor.clients;

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
public class NifiIngestion {
    private final WebClient webClient;
    @Value("${nifi.host}")
    private static String NIFI_HOST;
    private static final String NIFI_INGESTION_URL = "http://"+NIFI_HOST+":8002/track";

    @Autowired
    public NifiIngestion(WebClient webClient) {
        this.webClient = webClient;
    }

    // #TODO Handle if response is not in Map<String,Object>, by parsing response as string in reactive and converting it to Map<String,Object>
    public Mono<String> ingestEventToQueue(int accountId) {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("fields",new String[]{"idx"});
        return webClient
          .post()
          .uri(NIFI_INGESTION_URL)
          .header("account",String.valueOf(accountId))
          .body(BodyInserters.fromValue(requestBody))
          .retrieve()
          .bodyToMono(String.class);
        //.retryWhen(RetryUtil.retrySpec())
    }
}
