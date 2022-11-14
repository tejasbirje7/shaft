package org.shaft.administration.obligatory.tokens;

import com.nimbusds.jose.JOSEException;
import org.shaft.administration.obligatory.tokens.exceptions.ExpiredToken;
import org.shaft.administration.obligatory.tokens.exceptions.InvalidToken;
import org.shaft.administration.obligatory.tokens.services.GenerateToken;
import org.shaft.administration.obligatory.tokens.services.PKCSKeyGenerator;
import org.shaft.administration.obligatory.tokens.services.PKCSKeyLoader;
import org.shaft.administration.obligatory.tokens.services.ValidateToken;
import org.shaft.administration.obligatory.tokens.utils.APILog;
import org.shaft.administration.obligatory.tokens.utils.AppLogger;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class ShaftJWT {
    private final String keyPath;
    private PKCSKeyLoader pkcsKeyLoader = null;
    public static final String PKCS_LOADER = "load";
    public static final String PKCS_GENERATOR = "generate";



    public ShaftJWT(String keyPath, String mode) throws Exception {
        this.keyPath = keyPath;
        if (PKCS_LOADER.equals(mode)) {
            try {
                pkcsKeyLoader = new PKCSKeyLoader(keyPath);
                pkcsKeyLoader.load();
                if (pkcsKeyLoader == null) {
                    throw new Exception(APILog.LOG_034);
                }
            } catch (IOException e) {
                throw new IOException(APILog.LOG_035);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                throw new Exception(APILog.LOG_034);
            }
        }
    }

    public String generateToken(String audience, String issuer, Map<String,Object> claim, int expirationTime) throws Exception {
        return new GenerateToken(audience, expirationTime, issuer, claim, pkcsKeyLoader).issueToken();
    }

    public Map<String, Object> validateToken(String encryptedToken) throws InvalidToken, ExpiredToken, ParseException, JOSEException, Exception{
        try {
            return new ValidateToken(pkcsKeyLoader).verifyToken(encryptedToken);
        }
        catch (InvalidToken e) {
            AppLogger.debug(APILog.LOG_031);
            throw new InvalidToken(APILog.LOG_031);
        } catch (ExpiredToken e) {
            AppLogger.debug(APILog.LOG_032);
            throw new ExpiredToken(APILog.LOG_032);
        } catch (JOSEException | ParseException e) {
            AppLogger.debug(APILog.LOG_033);
            throw new ParseException(APILog.LOG_033,-1);
        } catch (Exception e) {
            AppLogger.debug(APILog.LOG_034);
            throw new Exception(APILog.LOG_034);
        }
    }

    public boolean generatePKCSKeys() throws Exception {
        try {
            new PKCSKeyGenerator().generateKeys(keyPath);
        } catch (IOException e) {
            AppLogger.debug(APILog.LOG_035);
            throw new Exception(APILog.LOG_035);
        }
        return true;
    }


    public static void main(String[] args) throws Exception {
        String keysPath = "/opt/springboot/unified/obligatory-services/src/main/java/org/shaft/administration/obligatory/tokens/keys";
        ShaftJWT shaftJWT = new ShaftJWT(keysPath,PKCS_LOADER);
        //shaftJWT.generatePKCSKeys();
        System.out.println(shaftJWT.generateToken("1600_readyToCook","",new HashMap<>(),80));
        //System.out.println(shaftJWT.validateToken("eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.Tn_PnLlmwPVa_QwMVBbi87sTse6LaH-74n3DDz5WJp4glRQ-zEus4nxYP9XgM907lqqJ-oBg9RCUX78RW4ugSOSGLjLVgA3BtGzfSuf684tkyqcsjS-nGJAv9lsQMGQn0DgCpwVdc3jEiC1wFSAT42v9krnUlwaaE-3bNFBchi1LYr0_hn2-V3sNom1xtWPo-rsNyOs9RDJKrUkNpjBmjC_ek4w8un2rK85cC22wf9Qbby2NgC3rkYAcua_EAuPGxZkfUH2b7XjG6pSrkLD-Tp1_yg_Dd4ayVFYcouxq8nIfkuIwTPItqGY5aJWwsV07YLL8a8GsfPdnL6u-OilGCw.VELDCyVFxGssj__GQKflIA.NjSV0MMkylEj8-a2OIkpTQyJsF_cc_vGP48HCVucOhfxc-gHQ05K6wvLiPblqd4bnqIycQx6wYfI2wkBNHYUDWHNHQBbZr8qESQ9zaMbrQ62mFjzJp-LhiDgLAaW2V5ahgTQoVJJfKVyDiwvHKMfsaFRuS8Qxci8dmkHe8YsbQdjIOyYt5aPoLBa69yN8WiRlX4jrGbq6QYVAzAbqth-uacO9ECDWd2r6HWnOGveVqahRTLQep83w_H2cLjpoIe24xbcF_nDBSNfrzcvyl2ZaaqbaUWCzZKhzpDD_Kts66WmZk1e76xndljfk6DKWP0LMw6319n4DcBqjzWsldCCRZfULTP-ZjFl8DFrbChDm8zKLrgE0pJdodzLO4n8zbX10EceMYzApBU3xgcCYyJmHpfLqTkv3hHwsD-WliaJtcZHWuJiG1dzk4VfXnLE7-MV2AM4e3kKEgaBXnZZqr7izdvhJi530XlLlj5R0_eJXe0TatImXGAdsWuF7qvLTmk7OkHBtdgliH0LZp5-ZpcNdx63kVtk_KzOIGxT_mMaGyEhzbRIOsfKV0Fq7V2w5tcdlOGAzjsSaG8xCGLcIMTIkD6Uznn9EWslLMbmPL4NMsvDqN_Ld5X7o_QgOIUkd-_K3rOex1Lqf8HOh4kG_Ht84_PNEZffQLggxafXkyG4_pJWdSdx43YTc8l0gwscCD1FHBZOY8BdWpi6BNY7P5os4tXBplHGMuVEJcIA5c-SFMywysL_HLP0D8W4l0byAL-SmJK0DmtjX-N7-lwCm1q5sQ.RhbPg4wNN_-vbjaZ3QeBHOpgtW8Pl-e-3eHeDXkTRWc"));
    }



}
