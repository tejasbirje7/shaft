package com.shaft.administration.accountmanagement.dao;

import java.util.Map;

public interface MetaDAO {
    public Map<String,Object> getMetaFields(int account, Map<String,Object> fields);
}
