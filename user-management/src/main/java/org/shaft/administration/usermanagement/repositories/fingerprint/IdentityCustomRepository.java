package org.shaft.administration.usermanagement.repositories.fingerprint;


import org.shaft.administration.usermanagement.entity.Identity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface IdentityCustomRepository {
    Flux<Identity> checkIfFpExistsForI(String fp, int i, boolean isIdentified);
    Flux<Identity> checkIfIExistsForFp(String fp);
    Mono<Long> updateFp(String fp, int i);
    Mono<Long>  upsertFpAndIPair(int account, String fp, long i);
    Mono<Identity> save(int accountId,Identity i);
}
