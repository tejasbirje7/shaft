package org.shaft.administration.reportingmanagement.dao;

import java.util.Map;
public interface QueryDao {
    Map<String,Object> getQueryResults(int accountId, Map<String,Object> rawQuery);
}
