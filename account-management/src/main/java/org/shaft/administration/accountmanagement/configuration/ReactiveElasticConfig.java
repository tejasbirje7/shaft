package org.shaft.administration.accountmanagement.configuration;

import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import javax.net.ssl.SSLContext;

public class ReactiveElasticConfig {

    @Value("${spring.elasticsearch.uris}")
    private String ELASTIC_URL;

    public ReactiveElasticsearchClient reactiveElasticsearchClient() throws Exception {

        SSLContextBuilder sslBuilder = SSLContexts.custom()
          .loadTrustMaterial(null, (x509Certificates, s) -> true);
        final SSLContext sslContext = sslBuilder.build();


        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
          .connectedTo(ELASTIC_URL)
          .usingSsl(sslContext)
          .withWebClientConfigurer(webClient -> {
              ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                  .maxInMemorySize(-1))
                .build();
              return webClient.mutate().exchangeStrategies(exchangeStrategies).build();
          })
          .build();

        return ReactiveRestClients.create(clientConfiguration);
    }

    public SimpleElasticsearchMappingContext elasticsearchMappingContext() {
        return new SimpleElasticsearchMappingContext();
    }


    public ElasticsearchConverter elasticsearchConverter() {
        return new MappingElasticsearchConverter(elasticsearchMappingContext());
    }

    @Bean
    public ReactiveElasticsearchOperations reactiveElasticsearchOperations() throws Exception {
        return new ReactiveElasticsearchTemplate(reactiveElasticsearchClient(), elasticsearchConverter());
    }

}
