package org.shaft.administration.eventingestion;

import org.shaft.administration.eventingestion.initKafka.StreamInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.shaft.administration")
public class EventIngestionApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(EventIngestionApplication.class);

	private final StreamInitializer streamInitializer;

	public EventIngestionApplication(StreamInitializer initializer) {
		this.streamInitializer = initializer;
	}

	public static void main(String[] args) {
		SpringApplication.run(EventIngestionApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		streamInitializer.init();
	}
}
