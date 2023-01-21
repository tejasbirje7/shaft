package org.shaft.administration.inventory.clients;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RestClient {

  private final WebClient webClient;
  private static final String PRODUCT_CATALOG_URL = "http://localhost:8081/catalog/items/bulk";
  private static final String CART_URL = "http://localhost:8083/cart/empty";

  public RestClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<String> getProducts(int accountId, List<String> iTemIds) {
    Map<String,Object> request = new HashMap<>();
    request.put("items",iTemIds);
    request.put("fields",new String[]{"id","name","description","category","gallery"});
    return webClient
      .post()
      .uri(PRODUCT_CATALOG_URL)
      .header("account",String.valueOf(accountId))
      .body(BodyInserters.fromValue(request))
      .retrieve()
      .bodyToMono(String.class);
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }

  public Mono<String> emptyCart(int accountId, int i) {
    return webClient
      .get()
      .uri(CART_URL)
      .header("account",String.valueOf(accountId))
      .header("i", String.valueOf(i))
      .retrieve()
      .bodyToMono(String.class);
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }
}
