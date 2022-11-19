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

    public String issueToken() {
        // #TODO Fill this claims map properly
        Map<String,Object> claims = new HashMap<>();
        claims.put("user","");
        claims.put("role","");
        claims.put("user-agent","");
        claims.put("publicIP","");
        claims.put("privateIP","");
        String token;
        try {
            token = this.shaftJWT.generateToken(
                    "1600_ready_to_cook",
                    "shaft.org",
                    claims,
                    60);
        } catch (Exception e) {
            // #TODO Handle this exception if keys are not found
            System.out.println("Exception while generating token" + e.getMessage());
            token = "";
        }
        return token;
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
