package org.shaft.administration.inventory.clients;

import org.springframework.beans.factory.annotation.Value;
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
  private final String PRODUCT_CATALOG_URL;
  private final String CART_HOST;

  public RestClient(WebClient webClient,
                    @Value("${shaft.services.products-catalog-url}") String productsCatalogURL,
                    @Value("${shaft.services.cart-management-url}") String cartHostURL) {
    this.webClient = webClient;
    this.PRODUCT_CATALOG_URL = productsCatalogURL;
    this.CART_HOST = cartHostURL;
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
      .uri(CART_HOST)
      .header("account",String.valueOf(accountId))
      .header("i", String.valueOf(i))
      .retrieve()
      .bodyToMono(String.class);
    //.retryWhen(RetryUtil.retrySpec()) // #TODO Add custom retry specs
  }
}
