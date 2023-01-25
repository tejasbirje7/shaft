package org.shaft.administration.usermanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.usermanagement.entity.Identity;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthDAO {
    Mono<ObjectNode> authenticateUser(Map<String,Object> request);
    Mono<ObjectNode> registerUser(int account, Map<String,Object> request);
}
