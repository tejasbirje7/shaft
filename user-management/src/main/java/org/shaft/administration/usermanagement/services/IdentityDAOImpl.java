package org.shaft.administration.usermanagement.services;


import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.shaft.administration.usermanagement.dao.IdentityDAO;
import org.shaft.administration.usermanagement.entity.Identity;
import org.shaft.administration.usermanagement.repositories.IdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class IdentityDAOImpl implements IdentityDAO {

    IdentityRepository identityRepository;
    private HttpHeaders httpHeaders;
    private ShaftJWT jwtUtil;
    private final RestTemplate restTemplate;
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public IdentityDAOImpl(IdentityRepository identityRepository, RestTemplate restTemplate) throws Exception {
        this.identityRepository = identityRepository;
        this.restTemplate = restTemplate;
        this.jwtUtil = new ShaftJWT();
    }

    // #TODO Handle identity scenario with fingerprint as well as IP, because IP changes is not very frequent
    @Override
    public Map<String, Integer> checkIdentity(int account,Map<String,Object> details) {
        ACCOUNT_ID.set(account);
        // #TODO Handle all exceptions and provide different response code for each exception
        String fp = (String) details.get("fp");
        Map<String,Integer> response;
        // Check if `i` exists in request
        if (details.containsKey("i")) {
            // `i` exists in request
            int i = Integer.parseInt((String) details.get("i"));
            boolean isIdentified = details.containsKey("isIdentified") && (boolean) details.get("isIdentified");
            List<Identity> fpDetails = identityRepository.checkIfFpExistsForI(fp,i,isIdentified);
            // Check if `fp` exists for `i` received in request
            if (fpDetails.isEmpty()) {
                // Update `fp` for this `i`
                Long totalUpdated = identityRepository.updateFp(fp,i);
                if (totalUpdated > 0) {
                    return (Map<String, Integer>) new HashMap<>().put("i",i);
                } else {
                    // #TODO Raise Exception
                }
            } else {
                // `fp` exists return `i`
                ACCOUNT_ID.remove();
                response = new HashMap<>();
                response.put("i",i);
                return response;
            }
        } else {
            // `i` doesn't exist in request
            List<Identity> fpDetails = identityRepository.checkIfIExistsForFp(fp);
            // Check if `i` exists for received `fp`
            if(fpDetails.isEmpty()) {
                // insert `fp` i.e. new `i` case

                Map<String,Object> request = new HashMap<>();
                request.put("fields",new String[]{"idx"});
                httpHeaders = new HttpHeaders();
                httpHeaders.set("account",String.valueOf(account));
                HttpEntity<Map<String,Object>> entity = new HttpEntity<>(request,httpHeaders);
                String idx = "";
                // Invoke API and parse response
                try {
                    ResponseEntity<ShaftResponseHandler> meta = restTemplate.exchange(
                            "http://localhost:8084/account/meta/fields",
                            HttpMethod.POST,entity,ShaftResponseHandler.class);
                    idx = (String) ((Map<String,Object>) Objects.requireNonNull(meta.getBody()).getData()).get("idx");
                } catch (Exception ex){
                    System.out.println(ex.getMessage());
                }

                if(!idx.isEmpty()) {
                    Identity newFpToI = new Identity();
                    if (details.containsKey("requestTime")) {
                        newFpToI.setIdentity((Integer) details.get("requestTime"));
                        newFpToI.setIdentified(false);
                        List<Map<String,String>> fpArray = new ArrayList<>();
                        fpArray.add((Map<String, String>) new HashMap<>().put("g",fp));
                        newFpToI.setFingerPrint(fpArray);
                        //identityRepository.save(newFpToI);
                        // #TODO  Insert the event schema into `idx` index fetched above to track events
                    } else {
                        ACCOUNT_ID.remove();
                        // #TODO Raise Exception BAD REQUEST
                    }
                } else {
                    ACCOUNT_ID.remove();
                    // #TODO Raise Exception ACCOUNT SERVICE DOWN
                }
            } else {
                // `i` exists return `fp`
                ACCOUNT_ID.remove();
                response = new HashMap<>();
                response.put("i",fpDetails.get(0).getIdentity());
                return response;
            }
        }
        ACCOUNT_ID.remove();
        return new HashMap<>();
    }

    /**
     * Don't expose user details via token, since this is invoked
     * without auth filter from app-gateway, so don't change return type.
     * It should strictly only return `i`
     * @param token
     * @return `i`
     */
    @Override
    public Map<String, Integer> getUserDetailsFromToken(String token) {
        Map<String,Integer> i = new HashMap<>();
        try {
            Map<String,Object> tokenDetails = this.jwtUtil.validateToken(token);
            if(tokenDetails.containsKey("i")) {
                i.put("i", (Integer) tokenDetails.get("i"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return i;
    }

    @Override
    public Map<String, Integer> upsertFpAndIPair(String fp, int i) {
        return null;
    }
}
