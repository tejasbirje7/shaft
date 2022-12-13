package com.shaft.administration.accountmanagement.dao;

import java.util.Map;

public interface DashboardDAO {
    boolean pinToDashboard(int accountId, Map<String,Object> rawQuery);
}
