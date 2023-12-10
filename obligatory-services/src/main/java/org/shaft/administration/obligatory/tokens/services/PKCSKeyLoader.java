package org.shaft.administration.obligatory.tokens.services;

import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.tokens.utils.APILog;
import org.shaft.administration.obligatory.tokens.utils.AppLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


@Slf4j // #TODO Use AppLogger and remove this annotation
public class PKCSKeyLoader {
    private RSAPublicKey publickey;

    private RSAPrivateKey privateKey;

    private final String keysPath;

    public PKCSKeyLoader(String keysPath) {
        this.keysPath = keysPath;
    }

    public void load() throws IOException, InvalidKeySpecException {
        AppLogger.debug(APILog.LOG_045);
        if (this.publickey == null && this.privateKey == null) {
            log.info("Keys Path {}",this.keysPath + File.separator + "public.key");
            byte[] encodedPublicKey = loadKeysFromSource(this.keysPath + File.separator + "public.key");
            byte[] encodedPrivateKey = loadKeysFromSource(this.keysPath + File.separator + "private.key");
            AppLogger.debug(APILog.LOG_048);
            KeyFactory keyFactory = null;
            try {
                String KEYPAIR_GENERATOR_ALGORITHM = "RSA";
                keyFactory = KeyFactory.getInstance(KEYPAIR_GENERATOR_ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
              encodedPublicKey);
            this.publickey = (RSAPublicKey)keyFactory.generatePublic(publicKeySpec);
            AppLogger.debug(APILog.LOG_049);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
              encodedPrivateKey);
            this.privateKey = (RSAPrivateKey)keyFactory.generatePrivate(privateKeySpec);
        }
    }

    public Object getPublickey() {
        return this.publickey;
    }

    public Object getPrivateKey() {
        return this.privateKey;
    }

    public byte[] loadKeysFromSource(String filePath) throws IOException {
        AppLogger.debug(APILog.LOG_046);
        File filePublicKey = new File(filePath);
        FileInputStream fis = new FileInputStream(filePath);
        byte[] encodedPublicKey = new byte[(int)filePublicKey.length()];
        // #TODO Remove below unnecessary reading of file
        fis.read(encodedPublicKey);
        fis.close();
        return encodedPublicKey;
    }
}
