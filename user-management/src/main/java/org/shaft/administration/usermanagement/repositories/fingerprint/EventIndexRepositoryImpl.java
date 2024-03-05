package org.shaft.administration.usermanagement.repositories.fingerprint;

import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.usermanagement.entity.EventIndex;
import org.shaft.administration.usermanagement.entity.Identity;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class EventIndexRepositoryImpl implements EventIndexRepository {
  private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;

  public EventIndexRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations) {
    this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
  }
  @Override
  public Mono<EventIndex> saveEventIndex(String indexName, EventIndex evtIdx) {
    return reactiveElasticsearchOperations.save(evtIdx,
      IndexCoordinates.of(indexName)
    ).doOnError(throwable -> log.error(throwable.getMessage(), throwable));
  }
}
