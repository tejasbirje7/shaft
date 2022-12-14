package org.shaft.administration.obligatory.auth.algorithms;


import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.shaft.administration.obligatory.auth.utils.APIConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Encryption {
    private Cipher cipher = null;

    private Logger logger = LoggerFactory.getLogger(Encryption.class);

    public Encryption() {
        try {
            Security.setProperty("crypto.policy", "unlimited");
            IvParameterSpec ivspec = new IvParameterSpec(APIConstant.iv);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(APIConstant.algorithm);
            KeySpec spec = new PBEKeySpec(APIConstant.secretKey.toCharArray(), APIConstant.salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.cipher.init(1, secretKey, ivspec);
        } catch (Exception e) {
            this.logger.error("Error occured while setting up encryption parameters", e.getCause());
        }
    }

    public String encrypt(String password) {
        String response = null;
        if (this.cipher == null) {
            this.logger.error("Cipher is not initialised properly.");
            return null;
        }
        try {
            response = Base64.getEncoder().encodeToString(this.cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
        } catch (IllegalBlockSizeException e) {
            this.logger.error("Error occured during encryption", e.getCause());
        } catch (BadPaddingException e) {
            this.logger.error("Error occured during encryption", e.getCause());
        }
        return response;
    }
}
