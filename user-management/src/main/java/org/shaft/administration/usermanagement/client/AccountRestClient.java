package org.shaft.administration.usermanagement.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AccountRestClient {
    private final WebClient webClient;
    private final String ACCOUNT_META_URL = "http://localhost:8084/account/meta/fields";

    @Autowired
    public AccountRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    // #TODO Handle if response is not in Map<String,Object>, by parsing response as string in reactive and converting it to Map<String,Object>
    public Mono<String> retrieveAccountMeta(int accountId) {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("fields",new String[]{"idx"});
        return webClient
                .post()
                .uri(ACCOUNT_META_URL)
                .header("account",String.valueOf(accountId))
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class);
                //.retryWhen(RetryUtil.retrySpec())
    }
}
