package org.shaft.administration.usermanagement.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.auth.transact.ShaftHashing;
import org.shaft.administration.obligatory.auth.utils.Mode;
import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.usermanagement.dao.AuthDAO;
import org.shaft.administration.usermanagement.entity.Identity;
import org.shaft.administration.usermanagement.entity.User;
import org.shaft.administration.usermanagement.repositories.AuthRepository;
import org.shaft.administration.usermanagement.repositories.IdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
  public Mono<Object> authenticateUser(Map<String,Object> request) {
    Map<String,Object> response = new HashMap<>();
    if (request.containsKey("details") && request.containsKey("fp")) {
      String email = ((Map<String,String>)request.get("details")).get("e");
      String password = ((Map<String,String>)request.get("details")).get("p");
      String fp = (String) request.get("fp");
      String hashedPassword = shaftHashing.transactPassword(Mode.ENCRYPT, password);
      return authRepository.findByEAndP(email,hashedPassword)
        .collectList()
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(userList -> {
          if (userList.size() > 0) {
            User user = userList.get(0);
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
              return identityRepository.upsertFpAndIPair(user.getA(),fp,user.getI())
                .map(totalUpdated -> {
                  Map<String,Object> updates = new HashMap<>();
                  if(totalUpdated > 0) {
                    updates = mapper.convertValue(user, new TypeReference<Map<String, Object>>() {});
                    updates.remove("p");
                    updates.put("tk",token);
                  }
                  return updates;
                }).block();
            } catch (Exception ex) {
              // #TODO Add retry mechanism
              System.out.println("Exception while upserting fpToI : " + ex.getMessage());
            }
          }
          return response;
        });
    }
    return Mono.just(response);
  }

  public Mono<Identity> registerUser(int account, Map<String,Object> request) {
    if (request.containsKey("details") && request.containsKey("fp")) {

      Map<String, String> details = (Map<String, String>) request.get("details");
      String email = details.get("e");
      int newI = Integer.parseInt(details.get("i"));
      String fp = (String) request.get("fp");

      return authRepository.countByE(email)
        .collectList()
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(userList->{
          if(!userList.isEmpty() && userList.get(0) > 0) {
            return null;
          } else {
            String password = details.get("p");
            String contact =  details.get("c");
            User user = new User();
            user.setI(newI);
            user.setC(Long.parseLong(contact));
            user.setA(account);
            user.setNm("Tejas Birje");
            user.setE(email);
            user.setP(shaftHashing.transactPassword(Mode.ENCRYPT, password));
            return user;
          }
        })
        .mapNotNull(user -> {
          User userObj = null;
          try {
            if(user != null) {
              userObj = authRepository.save(user)
                .onErrorResume(t -> {
                  if(t instanceof NoSuchIndexException) {
                    log.error("Exception - {} , No such index {}",t.getMessage(),ACCOUNT_ID.get());
                  }
                  if (t instanceof RestStatusException) {
                    // #TODO Check this exception which appears always even if document gets saved properly
                    log.error("RestStatusException {}",t.getMessage(),t);
                    return Mono.just(user);
                  }
                  return Mono.just(user);
                }).block(); // #TODO Add time duration to all block operations
            }
          } catch (RestStatusException rst) {
            return user;
          }
          return userObj;
        })
        .mapNotNull(user -> {
          if(user != null) {
            Identity i = getIdentityObject(fp,newI);
            return identityRepository.save(account,i)
              .onErrorResume(t -> {
                if(t instanceof NoSuchIndexException) {
                  log.error("Exception - {} , No such index {}",t.getMessage(),ACCOUNT_ID.get());
                }
                if (t instanceof RestStatusException) {
                  // #TODO Check this exception which appears always even if document gets saved properly
                  log.error("RestStatusException {}",t.getMessage(),t);
                  return Mono.just(i);
                }
                return Mono.just(new Identity());
              }).block();
          } else {
            return new Identity();
          }
        });
    }
    // #TODO Throw BAD_REQUEST exception
    return Mono.just(new Identity());
  }

  public Identity getIdentityObject(String fp, int newI) {
    List<Map<String,String>> guidDetails = new ArrayList<>();
    Map<String,String> g = new HashMap<>();
    g.put("g",fp);
    guidDetails.add(g);
    Identity i = new Identity();
    i.setIdentity(newI);
    i.setIdentified(true);
    i.setFingerPrint(guidDetails);
    return i;
  }
}
