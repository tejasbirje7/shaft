package org.shaft.administration.obligatory.auth.transact;

import org.shaft.administration.obligatory.auth.algorithms.Decryption;
import org.shaft.administration.obligatory.auth.algorithms.Encryption;
import org.shaft.administration.obligatory.auth.utils.APIConstant;
import org.shaft.administration.obligatory.auth.utils.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShaftHashing {
    Logger logger = LoggerFactory.getLogger(ShaftHashing.class);
    private Encryption enc;
    private Decryption dec;

    public ShaftHashing() {
        this.enc =new Encryption();
        this.dec =new Decryption();
    }

    public String transactPassword(Mode mode, String key){
        this.logger.debug(APIConstant.attribute);
        if (key.length() > 0 && mode != null) {
            try {
                this.logger.debug(APIConstant.processStarted + key);
                this.logger.debug(APIConstant.detectedMode + mode);
                if (mode.equals(Mode.ENCRYPT)) {
                    String hashedKey = this.enc.encrypt(key);
                    if (hashedKey != null) {
                        return hashedKey;
                    } else {
                        // #TODO Throw unable to encrypt key exception
                    }
                } else if (mode.equals(Mode.DECRYPT)) {
                    String decryptedString = this.dec.decrypt(key);
                    if (decryptedString != null) {
                        return decryptedString;
                    } else {
                        // #TODO Throw unable to decrypt key exception
                    }
                }
                this.logger.debug(APIConstant.processFinsished);
            } catch (Exception e) {
                // #TODO Throw generic exception
                this.logger.error(APIConstant.error + e.getMessage());
            }
        } else {
            // #TODO Throw empty attributes exception
            this.logger.error(APIConstant.attributeMissing);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(new ShaftHashing().transactPassword(Mode.DECRYPT,"nG//v18xUVqRA8xZaY2UJw=="));
    }
}
