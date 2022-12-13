package com.shaft.administration.accountmanagement.repositories.meta;

import com.shaft.administration.accountmanagement.entity.Meta;

import java.util.Map;

public interface MetaCustomRepository {
    public Meta getMetaFields(int account,String[] fields);
    Long pinToDashboard(int accountId, String query);
}
