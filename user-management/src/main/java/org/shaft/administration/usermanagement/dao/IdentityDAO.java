package org.shaft.administration.usermanagement.dao;

import java.util.Map;

public interface IdentityDAO {
    public Map<String,Integer> checkIdentity(int account,Map<String,Object> details);
    public Map<String,Integer> getUserDetailsFromToken(String token);
}
