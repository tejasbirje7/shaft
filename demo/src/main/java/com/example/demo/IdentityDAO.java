package com.example.demo;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface IdentityDAO {
    Mono<Map<String,Integer>> checkIdentity(int account, Map<String,Object> details);
    Map<String,Integer> getUserDetailsFromToken(String token);
    Map<String, Integer> upsertFpAndIPair(String fp, int i);
}
