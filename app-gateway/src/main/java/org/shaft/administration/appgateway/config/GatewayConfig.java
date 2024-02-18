package org.shaft.administration.appgateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

// #TODO Implement @EnableWebFluxSecurity here in gateway - https://reflectoring.io/spring-cors/
@Configuration
public class GatewayConfig {

  AuthenticationFilter authenticationFilter;

  @Autowired
  public GatewayConfig(AuthenticationFilter authenticationFilter) {
    this.authenticationFilter = authenticationFilter;
  }

  @Bean
  public CorsWebFilter corsWebFilter() {
    final CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(Collections.singletonList("*"));
    corsConfig.setMaxAge(3600L);
    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST"));
    corsConfig.addAllowedHeader("*");

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }

  /**
   * firstBackOff - Will wait for 100 ms whenever it is trying to initiate the first retry operation
   * maxBackOff - Will wait for maximum of 1000 ms between 2 retry operations
   * @param builder
   * @return
   */
  @Bean
  @Profile("prod")
  public RouteLocator routesForServiceConnect(RouteLocatorBuilder builder) {
    // #TODO Comment out filters once testing is finished
    return builder.routes()
      .route("products-catalog", r->r.path("/catalog/**")
        .filters(f -> f.filter(authenticationFilter)
          .retry(retryConfig -> retryConfig.setRetries(3)
            .setMethods(HttpMethod.GET)
            .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
          .circuitBreaker(config -> config.setName("productsCatalogCircuitBreaker")
            .setFallbackUri("forward:/circuit-breaker-fallback"))
          .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
            .setKeyResolver(userKeyResolver())))
        .uri("http://products-catalog:8088")) // 8088
      .route("inventory-management", r->r.path("/inventory/**")
        .filters(f -> f.filter(authenticationFilter)
          .retry(retryConfig -> retryConfig.setRetries(3)
            .setMethods(HttpMethod.GET)
            .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
          .circuitBreaker(config -> config.setName("productsCatalogCircuitBreaker")
            .setFallbackUri("forward:/circuit-breaker-fallback"))
          .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
            .setKeyResolver(userKeyResolver())))
        .uri("http://inventory-management:8082")) // 8082
      .route("cart-management", r->r.path("/cart/**")
        .filters(f -> f.filter(authenticationFilter)
          .retry(retryConfig -> retryConfig.setRetries(3)
            .setMethods(HttpMethod.GET)
            .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
          .circuitBreaker(config -> config.setName("productsCatalogCircuitBreaker")
            .setFallbackUri("forward:/circuit-breaker-fallback"))
          .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
            .setKeyResolver(userKeyResolver())))
        .uri("http://cart-management:8083")) // 8083
      .route("account-management", r->r.path("/account/**")
        .filters(f -> f.filter(authenticationFilter)
          .retry(retryConfig -> retryConfig.setRetries(3)
            .setMethods(HttpMethod.GET)
            .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
          .circuitBreaker(config -> config.setName("productsCatalogCircuitBreaker")
            .setFallbackUri("forward:/circuit-breaker-fallback"))
          .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
            .setKeyResolver(userKeyResolver())))
        .uri("http://account-management:8084")) // 8084
      .route("user-management", r->r.path("/user/**")
        .filters(f -> f.filter(authenticationFilter)
          .retry(retryConfig -> retryConfig.setRetries(3)
            .setMethods(HttpMethod.GET)
            .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
          .circuitBreaker(config -> config.setName("productsCatalogCircuitBreaker")
            .setFallbackUri("forward:/circuit-breaker-fallback"))
          .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
            .setKeyResolver(userKeyResolver())))
        .uri("http://user-management:8085")) // 8085
      .route("marketing-engine", r->r.path("/marketing/**")
        .filters(f -> f.filter(authenticationFilter)
          .retry(retryConfig -> retryConfig.setRetries(3)
            .setMethods(HttpMethod.GET)
            .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
          .circuitBreaker(config -> config.setName("productsCatalogCircuitBreaker")
            .setFallbackUri("forward:/circuit-breaker-fallback"))
          .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
            .setKeyResolver(userKeyResolver())))
        .uri("http://marketing-engine:8086")) // 8086
      .route("reporting-engine", r->r.path("/reporting/**")
        .filters(f -> f.filter(authenticationFilter)
          .retry(retryConfig -> retryConfig.setRetries(3)
            .setMethods(HttpMethod.GET)
            .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
          .circuitBreaker(config -> config.setName("productsCatalogCircuitBreaker")
            .setFallbackUri("forward:/circuit-breaker-fallback"))
          .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
            .setKeyResolver(userKeyResolver())))
        .uri("http://reporting-engine:8087")) // 8087
      .route("event-ingestion", r->r.path("/ingest/**")
        .filters(f -> f.filter(authenticationFilter)
          .retry(retryConfig -> retryConfig.setRetries(3)
            .setMethods(HttpMethod.GET)
            .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
          .circuitBreaker(config -> config.setName("productsCatalogCircuitBreaker")
            .setFallbackUri("forward:/circuit-breaker-fallback"))
          .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
            .setKeyResolver(userKeyResolver())))
        .uri("http://event-ingestion:8181")) // 8181
      .build();
  }

  @Bean
  @Profile("dev")
  public RouteLocator routesForEurekaServer(RouteLocatorBuilder builder) {
    return builder.routes()
      .route("event-ingestion", r->r.path("/ingest/**")
        .uri("lb://EVENT-INGESTION")) // 8181
      .route("inventory-management", r->r.path("/inventory/**")
        .uri("lb://INVENTORY-MANAGEMENT")) // 8082
      .route("cart-management", r->r.path("/cart/**")
        .uri("lb://CART-MANAGEMENT")) // 8083
      .route("account-management", r->r.path("/account/**")
        .uri("lb://ACCOUNT-MANAGEMENT")) // 8084
      .route("user-management", r->r.path("/user/**")
        .uri("lb://USER-MANAGEMENT")) // 8085
      .route("marketing-engine", r->r.path("/marketing/**")
        .uri("lb://MARKETING-ENGINE")) // 8086
      .route("reporting-engine", r->r.path("/reporting/**")
        .uri("lb://REPORTING-MANAGEMENT")) // 8087
      .route("products-catalog", r->r.path("/catalog/**")
        .uri("lb://products-catalog")) // 8088
      .route("customer-management", r->r.path("/customer/**")
        .uri("lb://CUSTOMER-MANAGEMENT")) // 8089
      .build();
  }

  @Bean
  public RedisRateLimiter redisRateLimiter() {
    return new RedisRateLimiter(10,1,1);
  }

  @Bean
  KeyResolver userKeyResolver() {
    return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
      .defaultIfEmpty("anonymous");
  }
}
