package org.shaft.administration.obligatory.auth.algorithms;

import org.shaft.administration.obligatory.auth.utils.APIConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class Decryption {
    private Cipher cipher = null;
    private Logger logger = LoggerFactory.getLogger(Decryption.class);

    public Decryption() {
        try {
            Security.setProperty("crypto.policy", "unlimited");
            IvParameterSpec ivspec = new IvParameterSpec(APIConstant.iv);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(APIConstant.algorithm);
            KeySpec spec = new PBEKeySpec(APIConstant.secretKey.toCharArray(), APIConstant.salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            this.cipher.init(2, secretKey, ivspec);
        } catch (Exception e) {
            this.logger.error("Error occured while setting up decryption parameters", e.getCause());
        }
    }

    public String decrypt(String encryptedKey) throws Exception {
        String response = null;
        if (this.cipher == null) {
            this.logger.error("Cipher is not initialised properly.");
            return null;
        }
        try {
            response = new String(this.cipher.doFinal(Base64.getDecoder().decode(encryptedKey)));
        } catch (IllegalBlockSizeException e) {
            this.logger.error("Error occured during decrypt", e.getCause());
        } catch (BadPaddingException e) {
            this.logger.error("Error occured during decrypt", e.getCause());
        }
        return response;
    }
}
