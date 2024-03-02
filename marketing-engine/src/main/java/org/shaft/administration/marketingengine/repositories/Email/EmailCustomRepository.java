package org.shaft.administration.marketingengine.repositories.Email;

import reactor.core.publisher.Mono;

public interface EmailCustomRepository {
  Mono<String> getQueryResults(int accountId, String query);
}
