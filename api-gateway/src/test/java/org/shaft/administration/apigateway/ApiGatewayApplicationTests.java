package org.shaft.administration.apigateway;

import org.junit.jupiter.api.Test;
import org.shaft.administration.apigateway.dao.AppMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTests {

	@Autowired
	private AppMappingService appMapping;

	@Test
	void contextLoads() {
	}

	@Test
	void testMapping() {
		appMapping.getMappings();
	}
}
