package org.shaft.administration.appgateway.config;

import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.obligatory.tokens.exceptions.ExpiredToken;
import org.shaft.administration.obligatory.tokens.exceptions.InvalidToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {
    private final ShaftJWT shaftJWT;
    private boolean invalidToken;
    private boolean expiredToken;

    // #TODO Remove dependency of keys file from app gateway. Let keys be in obligatory services
    public JWTUtil() {
        try {
            this.shaftJWT = new ShaftJWT();
        } catch (Exception e) {
            System.out.println("Not able to create shaftJWT");
            throw new RuntimeException(e);
        }

    }

    // #TODO Validate claims by comparing user-agent, publicIP, privateIP - it should match to request
    public Map<String,Object> validateToken(String encryptedToken) {
        try {
            return this.shaftJWT.validateToken(encryptedToken);
        } catch (InvalidToken e) {
            this.invalidToken = true;
        } catch (ExpiredToken e) {
            this.expiredToken = true;
        } catch (Exception e) {
            System.out.println("Exception while validating token");
        }
        return new HashMap<>();
    }

    public boolean isTokenValid() {
        return invalidToken;
    }

    public boolean isTokenExpired() {
        return expiredToken;
    }

}
