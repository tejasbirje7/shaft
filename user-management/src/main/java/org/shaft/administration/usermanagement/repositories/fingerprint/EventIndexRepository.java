package org.shaft.administration.usermanagement.repositories.fingerprint;

import org.shaft.administration.usermanagement.entity.EventIndex;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EventIndexRepository {
  Mono<EventIndex> saveEventIndex(String indexName, EventIndex evtIdx);
}
