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
                .route("orderId", r->r.path("/catalog/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://PRODUCT-CATALOG"))
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://user-service"))
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://auth-service"))
                .build();
        /*
        return builder.routes()
				.route("orderId", r->r.path("/catalog/**").uri("lb://PRODUCT-CATALOG"))
				.build();
         */
    }
}
