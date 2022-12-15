package org.shaft.administration.usermanagement.dao;

import java.util.Map;

public interface IdentityDAO {
    Map<String,Integer> checkIdentity(int account,Map<String,Object> details);
    Map<String,Integer> getUserDetailsFromToken(String token);
    Map<String, Integer> upsertFpAndIPair(String fp, int i);
}
