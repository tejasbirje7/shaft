package org.shaft.administration.apigateway.dao;

import java.util.Map;

public interface FingerPrintingDAO {
    public Map<String,Integer> checkIdentity(Map<String,Object> details);
}
