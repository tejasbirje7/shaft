package com.shaft.administration.accountmanagement.dao;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface DashboardDAO {
    Mono<Boolean> pinToDashboard(int accountId, Map<String,Object> rawQuery);
}
