package org.shaft.administration.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;


@SpringBootApplication
@EnableEurekaClient
@EnableReactiveElasticsearchRepositories
public class ProductCatalogApplication {

	public static void main(String[] args) {SpringApplication.run(ProductCatalogApplication.class, args);}

}

