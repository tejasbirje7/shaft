package org.shaft.administration.usermanagement.services;

import com.netflix.discovery.converters.Auto;
import org.shaft.administration.obligatory.auth.transact.ShaftHashing;
import org.shaft.administration.obligatory.auth.utils.Mode;
import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.usermanagement.dao.AuthDAO;
import org.shaft.administration.usermanagement.entity.User;
import org.shaft.administration.usermanagement.repositories.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthDAOImpl implements AuthDAO {

    ShaftHashing shaftHashing;
    AuthRepository authRepository;

    ShaftJWT shaftJWT;

    @Autowired
    public AuthDAOImpl(AuthRepository authRepository) throws Exception {
        this.shaftHashing = new ShaftHashing();
        this.shaftJWT = new ShaftJWT();
        this.authRepository = authRepository;
    }

    @Override
    public void authenticateUser(Map<String,Object> request) {
        if (request.containsKey("details") && request.containsKey("fp")) {
            String email = ((Map<String,String>)request.get("details")).get("e");
            String password = ((Map<String,String>)request.get("details")).get("p");
            String fp = (String) request.get("fp");
            String hashedPassword = shaftHashing.transactPassword(Mode.ENCRYPT, password);
            User user = authRepository.findByEAndP(email,hashedPassword);
            System.out.println(user);
            if (user == null) {
                // #TODO Wrong credentials
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
                    System.out.println("Exception while generating token" + e.getMessage());
                    token = "";
                }

            }
        }
    }

    public void registerUser() {

    }
}
