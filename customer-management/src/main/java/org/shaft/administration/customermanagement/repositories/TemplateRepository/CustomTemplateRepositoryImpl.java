package org.shaft.administration.customermanagement.repositories.TemplateRepository;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.shaft.administration.customermanagement.entity.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Slf4j
@Repository
public class CustomTemplateRepositoryImpl implements CustomTemplateRepository{
  private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;
  private final ReactiveElasticsearchClient reactiveElasticsearchClient;
  private QueryBuilder query;
  private NativeSearchQuery ns;
  HttpHeaders httpHeaders;
  @Value("${spring.elasticsearch.host}")
  private String elasticsearchHost;

  @Autowired
  public CustomTemplateRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations,
                                      RestTemplate restTemplate,
                                      ReactiveElasticsearchClient reactiveElasticsearchClient) {
    this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
    this.reactiveElasticsearchClient = reactiveElasticsearchClient;
    httpHeaders = new HttpHeaders();
  }

  @Override
  public Flux<Template> getTemplatesCatalog() {
    query = QueryBuilders.matchAllQuery();
    ns = new NativeSearchQueryBuilder()
      .withQuery(query)
      .withMaxResults(100)
      .withPageable(PageRequest.of(0,50))
      .build();

    return reactiveElasticsearchOperations.search(ns, Template.class,
        IndexCoordinates.of("templates_catalog"))
      .map(SearchHit::getContent)
      .filter(Objects::nonNull)
      .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
  }
}
