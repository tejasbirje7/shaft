package org.shaft.administration.reportingmanagement.repositories.segment;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.shaft.administration.reportingmanagement.entity.Segment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Slf4j
@Repository
public class SegmentCustomRepositoryImpl implements SegmentCustomRepository {
  private QueryBuilder query;
  private NativeSearchQuery ns;
  private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;

  public SegmentCustomRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations) {
    this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
  }
  @Override
  public Flux<Segment> getSegments(String[] fields) {
    if(fields.length > 0) {
      final SourceFilter filter = new FetchSourceFilter(fields, null);
      ns = new NativeSearchQueryBuilder()
        .withQuery(query)
        .withMaxResults(100)
        .withPageable(PageRequest.of(0,50))
        .withSourceFilter(filter)
        .build();
    } else {
      query = QueryBuilders.matchAllQuery();
      ns = new NativeSearchQueryBuilder()
        .withQuery(query)
        .withMaxResults(100)
        .withPageable(PageRequest.of(0,50))
        .build();
    }
    return reactiveElasticsearchOperations.search(ns, Segment.class)
      .map(SearchHit::getContent)
      .filter(Objects::nonNull)
      .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
  }
}
