package org.shaft.administration.usermanagement.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import org.shaft.administration.obligatory.auth.transact.ShaftHashing;
import org.shaft.administration.obligatory.auth.utils.Mode;
import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.usermanagement.dao.AuthDAO;
import org.shaft.administration.usermanagement.entity.Identity;
import org.shaft.administration.usermanagement.entity.User;
import org.shaft.administration.usermanagement.repositories.AuthRepository;
import org.shaft.administration.usermanagement.repositories.IdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthDAOImpl implements AuthDAO {

    ShaftHashing shaftHashing;
    AuthRepository authRepository;
    IdentityRepository identityRepository;
    ShaftJWT shaftJWT;
    ObjectMapper mapper;

    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public AuthDAOImpl(AuthRepository authRepository, IdentityRepository identityRepository) throws Exception {
        this.shaftHashing = new ShaftHashing();
        this.shaftJWT = new ShaftJWT();
        this.authRepository = authRepository;
        this.identityRepository = identityRepository;
        this.mapper = new ObjectMapper();
    }

    @Override
    public Map<String,Object> authenticateUser(Map<String,Object> request) {
        Map<String,Object> response = new HashMap<>();
        if (request.containsKey("details") && request.containsKey("fp")) {
            String email = ((Map<String,String>)request.get("details")).get("e");
            String password = ((Map<String,String>)request.get("details")).get("p");
            String fp = (String) request.get("fp");
            String hashedPassword = shaftHashing.transactPassword(Mode.ENCRYPT, password);
            User user = authRepository.findByEAndP(email,hashedPassword);
            if (user == null) {
                // #TODO Wrong credentials
                response = new HashMap<>();
            } else {
                // #TODO Fill this claims map properly
                Map<String,Object> claims = new HashMap<>();
                claims.put("user",user.getE());
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
                    System.out.println("Exception while generating token : " + e.getMessage());
                    throw new RuntimeException("Failed to create token");
                }
                try {
                    // #TODO Make this call asynchronous
                    identityRepository.upsertFpAndIPair(user.getA(),fp,user.getI());
                } catch (Exception ex) {
                    // #TODO Add retry mechanism
                    System.out.println("Exception while upserting fpToI : " + ex.getMessage());
                }
                response = mapper.convertValue(user, new TypeReference<Map<String, Object>>() {});
                response.remove("p");
                response.put("tk",token);
            }
        } else {
            // #TODO Throw BAD_REQUEST exception
        }
        return response;
    }

    public void registerUser(int account, Map<String,Object> request) {
        Map<String,Object> response = new HashMap<>();
        if (request.containsKey("details") && request.containsKey("fp")) {
            Map<String, String> details = (Map<String, String>) request.get("details");
            String email = details.get("e");
            String password = details.get("p");
            String contact =  details.get("c");
            int newI = Integer.parseInt(details.get("i"));
            String fp = (String) request.get("fp");
            long userCount = authRepository.countByE(email);
            if ( userCount > 0 ) {
                // #TODO Throw exception user exists
                response = new HashMap<>();
            } else {
                String hashedPassword = shaftHashing.transactPassword(Mode.ENCRYPT, password);
            }
            List<Map<String,String>> guidDetails = new ArrayList<>();
            Map<String,String> g = new HashMap<>();
            g.put("g",fp);
            guidDetails.add(g);
            Identity i = new Identity();
            i.setIdentity(newI);
            i.setIdentified(true);
            i.setFingerPrint(guidDetails);
            identityRepository.save(i);
        } else {
            // #TODO Throw BAD_REQUEST exception
        }
    }
}
