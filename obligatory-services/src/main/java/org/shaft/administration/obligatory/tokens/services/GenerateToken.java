package org.shaft.administration.obligatory.tokens.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.shaft.administration.obligatory.tokens.utils.APILog;
import org.shaft.administration.obligatory.tokens.utils.AppLogger;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GenerateToken {

    private String audience = null;

    private final int expirationTime;

    private String issuer = null;

    private Map<String,Object> claim = null;

    private PKCSKeyLoader pkcsKeyLoader = null;

    private JWSHeader jwsHeader = null;

    public JWSAlgorithm JWS_ALGORITHM = JWSAlgorithm.RS256;

    public JWEAlgorithm JWE_ALGORITHM = JWEAlgorithm.RSA_OAEP_256;

    public EncryptionMethod ENCRYPTION_METHOD = EncryptionMethod.A256CBC_HS512;

    public String JWE_CONTENT_TYPE = "JWT";

    public GenerateToken(String audience, int expirationTime, String issuer, Map<String,Object> claim, PKCSKeyLoader pkcsKeyLoader) {
        this.audience = audience;
        this.expirationTime = expirationTime;
        this.issuer = issuer;
        this.claim = claim;
        this.pkcsKeyLoader = pkcsKeyLoader;
    }

    public String issueToken() throws Exception {
        AppLogger.debug(APILog.LOG_011);
        AppLogger.debug(APILog.LOG_014);
        SignedJWT signedJWT = new SignedJWT(getHeader(), getClaimSet());
        signedJWT.sign(new RSASSASigner((PrivateKey) this.pkcsKeyLoader.getPrivateKey()));
        AppLogger.debug(APILog.LOG_015);
        JWEObject jweObject = new JWEObject((
                new JWEHeader.Builder(this.JWE_ALGORITHM, this.ENCRYPTION_METHOD))
                .contentType(this.JWE_CONTENT_TYPE)
                .build(),
                new Payload(signedJWT));
        AppLogger.debug(APILog.LOG_016);
        jweObject.encrypt(new RSAEncrypter((RSAPublicKey) this.pkcsKeyLoader.getPublickey()));
        AppLogger.debug(APILog.LOG_017);
        return jweObject.serialize();
    }

    private JWTClaimsSet getClaimSet() {
        AppLogger.debug(APILog.LOG_012);
        String claim = this.claim.keySet().stream()
                .map(key -> key + "=" + this.claim.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return (new JWTClaimsSet.Builder())
                .issuer(this.issuer)
                .audience(Arrays.asList(this.audience.trim().split(",")))
                .expirationTime(new Date((new Date()).getTime() + (
                        this.expirationTime * 1000L)))
                .issueTime(new Date((new Date()).getTime()))
                .jwtID(UUID.randomUUID().toString())
                .claim(claim, Boolean.TRUE)
                .build();
    }

    private JWSHeader getHeader() {
        if (this.jwsHeader == null)
            this.jwsHeader = (new JWSHeader.Builder(
                    this.JWS_ALGORITHM))
                    .keyID(((RSAPrivateKey)this.pkcsKeyLoader.getPrivateKey()).getFormat())
                    .build();
        return this.jwsHeader;
    }
}
