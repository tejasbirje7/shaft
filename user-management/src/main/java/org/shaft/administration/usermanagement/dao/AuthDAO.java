package org.shaft.administration.usermanagement.dao;

import java.util.Map;

public interface AuthDAO {
    void authenticateUser(Map<String,Object> request);
}
