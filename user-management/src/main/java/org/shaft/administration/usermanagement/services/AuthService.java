package org.shaft.administration.usermanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.auth.transact.ShaftHashing;
import org.shaft.administration.obligatory.auth.utils.APIConstant;
import org.shaft.administration.obligatory.auth.utils.Mode;
import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.usermanagement.constants.API;
import org.shaft.administration.usermanagement.constants.ResponseCode;
import org.shaft.administration.usermanagement.dao.AuthDAO;
import org.shaft.administration.usermanagement.entity.Identity;
import org.shaft.administration.usermanagement.entity.User;
import org.shaft.administration.usermanagement.repositories.AuthRepository;
import org.shaft.administration.usermanagement.repositories.IdentityRepository;
import org.shaft.administration.usermanagement.constants.Log;
import org.shaft.administration.usermanagement.util.ResponseBuilder;
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
public class AuthService implements AuthDAO {

  ShaftJWT shaftJWT;
  ObjectMapper mapper;
  ShaftHashing shaftHashing;
  AuthRepository authRepository;
  IdentityRepository identityRepository;
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

  @Autowired
  public AuthService(AuthRepository authRepository,
                     IdentityRepository identityRepository) throws Exception {
    this.shaftJWT = new ShaftJWT();
    this.mapper = new ObjectMapper();
    this.shaftHashing = new ShaftHashing();
    this.authRepository = authRepository;
    this.identityRepository = identityRepository;
  }

  @Override
  public Mono<ObjectNode> authenticateUser(Map<String,Object> request) {
    if (request.containsKey(API.DETAILS) && request.containsKey(API.FINGER_PRINT)) {
      String email = ((Map<String,String>)request.get(API.DETAILS)).get(API.EMAIL);
      String password = ((Map<String,String>)request.get(API.DETAILS)).get(API.PASSWORD);
      String fp = (String) request.get(API.FINGER_PRINT);
      String hashedPassword = shaftHashing.transactPassword(Mode.ENCRYPT, password);
      // Check if user is present in system
      return authRepository.findByEAndP(email,hashedPassword)
        .collectList()
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(userList -> {
          if (userList.size() > 0) {
            User user = userList.get(0);
            String token;
            try {
              token = this.generateToken(user.getE());
            } catch (Exception e) {
              log.error(Log.TOKEN_GENERATION_FAILED + e.getMessage());
              return ResponseBuilder.buildResponse(ResponseCode.TOKEN_GENERATION_FAILED);
            }
            // #TODO Add retry spec if upsert failed with inbuilt method .retry(RetrySpec retrySpec)
            // Upsert FP to I identity if it's new fp for i
            return identityRepository.upsertFpAndIPair(user.getA(),fp,user.getI())
              .map(totalUpdated -> {
                ObjectNode response = interceptUserResponse(user,token);
                return ResponseBuilder.buildResponse(ResponseCode.LOGIN_SUCCESS,response);
              })
              .onErrorResume(t -> {
                log.error(Log.FP_TO_I_UPSERT_FAILED + t.getMessage());
                return Mono.just(ResponseBuilder.buildResponse(ResponseCode.IDENTITY_UPDATE_FAILED));
              }).block();
          } else {
            return ResponseBuilder.buildResponse(ResponseCode.USER_NOT_FOUND);
          }
        })
        .onErrorResume(t-> {
          log.error(Log.FETCHING_USER_EXCEPTION,t);
          return Mono.just(ResponseBuilder.buildResponse(ResponseCode.UNABLE_TO_FETCH_USER));
        });
    } else {
      return Mono.just(ResponseBuilder.buildResponse(ResponseCode.BAD_REQUEST));
    }
  }

  public Mono<Identity> registerUser(int account, Map<String,Object> request) {
    if (request.containsKey(API.DETAILS) && request.containsKey(API.FINGER_PRINT)) {
      Map<String, String> details = (Map<String, String>) request.get(API.DETAILS);
      String email = details.get(API.EMAIL);
      int newI = Integer.parseInt(details.get(API.IDENTITY));
      String fp = (String) request.get(API.FINGER_PRINT);
      return authRepository.countByE(email)
        .collectList()
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(userList -> {
          if(!userList.isEmpty() && userList.get(0) > 0) {
            // User already exists
            return null;
            //return Mono.just(ResponseBuilder.buildResponse(ResponseCode.USER_EXISTS));
          } else {
            User user = new User();
            user.setI(newI);
            user.setC(Long.parseLong(details.get(API.CONTACT)));
            user.setA(account);
            user.setNm("Tejas Birje");
            user.setE(email);
            user.setP(shaftHashing.transactPassword(Mode.ENCRYPT, details.get(API.PASSWORD)));




            authRepository.save(user)
              .publishOn(Schedulers.boundedElastic())
              .map(user2 -> {
                Identity i = getIdentityObject(fp,newI);
                identityRepository.save(account,i)
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
              })
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
              }).block();


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
                }).block();
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

  private Identity getIdentityObject(String fp, int newI) {
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

  private String generateToken(String email) throws Exception {
    // #TODO Fill this claims map properly
    Map<String,Object> claims = new HashMap<>();
    claims.put("user",email);
    claims.put("role","");
    claims.put("user-agent","");
    claims.put("publicIP","");
    claims.put("privateIP","");
    String token;
    token = this.shaftJWT.generateToken(
      "1600_ready_to_cook",
      "shaft.org",
      claims,
      60);
    return token;
  }

  private ObjectNode interceptUserResponse(User user, String token) {
    ObjectNode response = mapper.convertValue(user, ObjectNode.class);
    response.remove("p");
    response.put("tk",token);
    return response;
  }


}
