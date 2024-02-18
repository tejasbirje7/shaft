package org.shaft.administration.customermanagement.repositories.TemplateConfigRepository;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.xcontent.XContentType;
import org.shaft.administration.customermanagement.entity.TemplateConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.RestStatusException;
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
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class CustomTemplateConfigRepositoryImpl implements CustomTemplateConfigRepository {
  private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;
  private final ReactiveElasticsearchClient reactiveElasticsearchClient;
  private QueryBuilder query;
  private NativeSearchQuery ns;
  HttpHeaders httpHeaders;
  @Value("${spring.elasticsearch.host}")
  private String elasticsearchHost;
  @Autowired
  public CustomTemplateConfigRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations,
                                            RestTemplate restTemplate,
                                            ReactiveElasticsearchClient reactiveElasticsearchClient) {
    this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
    this.reactiveElasticsearchClient = reactiveElasticsearchClient;
    httpHeaders = new HttpHeaders();
  }
  @Override
  public Flux<TemplateConfiguration> getTemplateConfiguration(int accountId) {
    query = QueryBuilders.matchAllQuery();
    ns = new NativeSearchQueryBuilder()
      .withQuery(query)
      .withMaxResults(100)
      .withPageable(PageRequest.of(0,50))
      .build();

    return reactiveElasticsearchOperations.search(ns, TemplateConfiguration.class,
        IndexCoordinates.of(accountId+"_template_configuration"))
      .map(SearchHit::getContent)
      .filter(Objects::nonNull)

      .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
  }

  @Override
  public Mono<TemplateConfiguration> save(int accountId, TemplateConfiguration config) {
    return reactiveElasticsearchOperations.save(config,
      IndexCoordinates.of(accountId + "_template_configuration")
    ).doOnError(throwable -> log.error(throwable.getMessage(), throwable));
  }

  @Override
  public Mono<Long> update(int accountId, Map<String,Object> config) {
    String index = accountId + "_template_configuration";
    UpdateRequest updateRequest = new UpdateRequest(index, (String) config.get("templateId"));
    updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
    updateRequest.doc(config,XContentType.JSON);
    return reactiveElasticsearchClient.update(updateRequest)
      .map(response -> response != null ? 1L : 0L)
      .filter(Objects::nonNull)
      .doOnError(throwable -> {
        if(!(throwable instanceof RestStatusException)) {
          log.error(throwable.getMessage(), throwable);
        }
      });
  }

}
