package com.example.demo;


import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public interface IdentityCustomRepository {
    Flux<Identity> checkIfFpExistsForI(String fp, int i, boolean isIdentified);
    Flux<Identity> checkIfIExistsForFp(String fp);
    Long updateFp(String fp,int i);
    Long upsertFpAndIPair(int account, String fp, int i);
}
