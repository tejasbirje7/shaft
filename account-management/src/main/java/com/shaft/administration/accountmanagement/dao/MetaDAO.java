package com.shaft.administration.accountmanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface MetaDAO {
    Mono<ObjectNode> getMetaFields(int account, Map<String,Object> fields);
    Mono<ObjectNode> getEventsMeta(int account);
}
