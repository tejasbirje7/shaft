package org.shaft.administration.eventprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.shaft.administration")
public class EventProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventProcessorApplication.class, args);
	}

}
