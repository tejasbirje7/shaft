package org.shaft.administration.usermanagement.dao;

import java.util.Map;

public interface AuthDAO {
    Map<String,Object> authenticateUser(Map<String,Object> request);
}
