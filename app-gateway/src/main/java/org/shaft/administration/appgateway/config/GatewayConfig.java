package org.shaft.administration.appgateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

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
        source.registerCorsConfiguration("/*", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        // #TODO Comment out filters once testing is finished
        return builder.routes()
                .route("product-catalog", r->r.path("/catalog/**")
                        .uri("lb://PRODUCT-CATALOG")) // 8081
                        //.filters(f -> f.filter(authenticationFilter))
                .route("inventory-management", r->r.path("/inventory/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://INVENTORY-MANAGEMENT")) // 8082
                .route("cart-management", r->r.path("/cart/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://CART-MANAGEMENT")) // 8083
                .route("account-management", r->r.path("/account/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://ACCOUNT-MANAGEMENT")) // 8084
                .route("user-management", r->r.path("/user/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://USER-MANAGEMENT")) // 8085
                .build();
    }
}
