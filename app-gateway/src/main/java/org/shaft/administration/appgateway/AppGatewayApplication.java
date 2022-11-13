package org.shaft.administration.appgateway;

import org.shaft.administration.appgateway.config.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;


@EnableEurekaClient
@EnableHystrix
@EnableHystrixDashboard
@SpringBootApplication
public class AppGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppGatewayApplication.class, args);
	}

	AuthenticationFilter authenticationFilter;

	@Autowired
	public AppGatewayApplication(AuthenticationFilter authenticationFilter) {
		this.authenticationFilter = authenticationFilter;
	}

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("orderId", r->r.path("/catalog/**").uri("lb://PRODUCT-CATALOG"))
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

	@Bean
	public AuthenticationFilter getAuthenticationFilter() {
		return new AuthenticationFilter();
	}

}
