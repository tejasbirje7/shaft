package org.shaft.administration.usermanagement.dao;

import org.shaft.administration.usermanagement.entity.Identity;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthDAO {
    Mono<Object> authenticateUser(Map<String,Object> request);
    Mono<Identity> registerUser(int account, Map<String,Object> request);
}
