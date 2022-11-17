package org.shaft.administration.appgateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    AuthenticationFilter authenticationFilter;

    @Autowired
    public GatewayConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-catalog", r->r.path("/catalog/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://PRODUCT-CATALOG")) // 8081
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
