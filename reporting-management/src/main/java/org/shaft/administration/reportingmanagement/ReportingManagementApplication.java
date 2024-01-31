package org.shaft.administration.reportingmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;

@SpringBootApplication
//@EnableEurekaClient
@EnableReactiveElasticsearchRepositories
public class ReportingManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportingManagementApplication.class, args);
	}

}
