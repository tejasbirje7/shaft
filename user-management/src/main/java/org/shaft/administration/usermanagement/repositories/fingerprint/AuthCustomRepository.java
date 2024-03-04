package org.shaft.administration.usermanagement.repositories.fingerprint;

import reactor.core.publisher.Mono;

public interface AuthCustomRepository {
  Mono<Long> updateEmail(String account,String email, int i);
}
