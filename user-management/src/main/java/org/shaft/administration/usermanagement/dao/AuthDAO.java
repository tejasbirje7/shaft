package org.shaft.administration.usermanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.usermanagement.entity.Identity;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthDAO {
    Mono<ObjectNode> authenticateUser(ObjectNode request);
    Mono<ObjectNode> registerUser(int account, ObjectNode request);
}
