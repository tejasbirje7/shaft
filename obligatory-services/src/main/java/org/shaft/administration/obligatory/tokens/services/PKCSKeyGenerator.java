package org.shaft.administration.obligatory.tokens.services;


import org.shaft.administration.obligatory.tokens.utils.APILog;
import org.shaft.administration.obligatory.tokens.utils.AppLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class PKCSKeyGenerator {
    String KEYPAIR_GENERATOR_ALGORITHM = "RSA";

    public void generateKeys(String filepath) throws IOException {
        AppLogger.debug(String.valueOf(APILog.LOG_040) + filepath + File.separator + "public.key");
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(this.KEYPAIR_GENERATOR_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGenerator.initialize(2048);
        AppLogger.debug(APILog.LOG_041);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        AppLogger.debug(APILog.LOG_042);
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        AppLogger.debug(APILog.LOG_043);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(String.valueOf(filepath) + File.separator + "public.key");
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();
        AppLogger.debug(APILog.LOG_044);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
        fos = new FileOutputStream(String.valueOf(filepath) + File.separator + "private.key");
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }
}
