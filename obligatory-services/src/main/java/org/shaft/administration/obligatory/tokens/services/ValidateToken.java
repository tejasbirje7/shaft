package org.shaft.administration.obligatory.tokens.services;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.shaft.administration.obligatory.tokens.exceptions.ExpiredToken;
import org.shaft.administration.obligatory.tokens.exceptions.InvalidToken;
import org.shaft.administration.obligatory.tokens.utils.APILog;
import org.shaft.administration.obligatory.tokens.utils.AppLogger;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ValidateToken {

    private final PKCSKeyLoader pkcsKeyLoader;

    public ValidateToken(PKCSKeyLoader pkcsKeyLoader) {
        this.pkcsKeyLoader = pkcsKeyLoader;
    }

    public Map<String,Object> verifyToken(String encryptedToken) throws InvalidToken, ExpiredToken, ParseException, JOSEException, Exception {
        AppLogger.debug(APILog.LOG_001);
        JWEObject jweObject = JWEObject.parse(encryptedToken);
        AppLogger.debug(APILog.LOG_002);
        jweObject.decrypt(new RSADecrypter((RSAPrivateKey) this.pkcsKeyLoader.getPrivateKey()));
        AppLogger.debug(APILog.LOG_003);
        SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
        AppLogger.debug(APILog.LOG_005);
        if ((new Date()).before(signedJWT.getJWTClaimsSet().getExpirationTime())) {
            AppLogger.debug(APILog.LOG_006);
            if (signedJWT.verify(new RSASSAVerifier((RSAPublicKey) this.pkcsKeyLoader.getPublickey()))) {
                AppLogger.debug(APILog.LOG_007);
                return signedJWT.getJWTClaimsSet().getClaims();
            }
            AppLogger.debug(APILog.LOG_008);
            throw new InvalidToken("Invalid Token");
        }
        AppLogger.debug(APILog.LOG_009);
        throw new ExpiredToken("Expired Token");
    }

}
