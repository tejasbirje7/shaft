package org.shaft.administration.usermanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IdentityDAO {
    Mono<ObjectNode> checkIdentity(int account, Map<String,Object> details);
    Map<String,Integer> getUserDetailsFromToken(String token);
    Map<String, Integer> upsertFpAndIPair(String fp, int i);
}
