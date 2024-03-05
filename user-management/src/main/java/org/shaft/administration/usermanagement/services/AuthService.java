package org.shaft.administration.usermanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.auth.transact.ShaftHashing;
import org.shaft.administration.obligatory.auth.utils.Mode;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.usermanagement.clients.AccountRestClient;
import org.shaft.administration.usermanagement.constants.UserConstants;
import org.shaft.administration.usermanagement.constants.UserManagementLogs;
import org.shaft.administration.usermanagement.dao.AuthDAO;
import org.shaft.administration.usermanagement.entity.EventIndex;
import org.shaft.administration.usermanagement.entity.Identity;
import org.shaft.administration.usermanagement.entity.User;
import org.shaft.administration.usermanagement.repositories.AuthRepository;
import org.shaft.administration.usermanagement.repositories.IdentityRepository;
import org.shaft.administration.usermanagement.repositories.fingerprint.EventIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
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

  private final ShaftJWT shaftJWT;
  private final ObjectMapper mapper;
  private final ObjectReader mapParser;
  private final ShaftHashing shaftHashing;
  private final AuthRepository authRepository;
  private final IdentityRepository identityRepository;
  private final AccountRestClient accountRestClient;
  private final EventIndexRepository eventIndexRepository;
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

  @Autowired
  public AuthService(AuthRepository authRepository,
                     ShaftHashing shaftHashing,
                     AccountRestClient accountRestClient,
                     IdentityRepository identityRepository, EventIndexRepository eventIndexRepository) throws Exception {
    this.eventIndexRepository = eventIndexRepository;
    this.shaftJWT = new ShaftJWT();
    this.mapper = new ObjectMapper();
    this.shaftHashing = shaftHashing;
    this.authRepository = authRepository;
    this.accountRestClient = accountRestClient;
    this.identityRepository = identityRepository;
    this.mapParser = new ObjectMapper().readerFor(Map.class);
  }

  @Override
  public Mono<ObjectNode> authenticateUser(ObjectNode request) {
    if (request.has(UserConstants.DETAILS) && request.has(UserConstants.FINGER_PRINT)) {
      JsonNode details = request.get(UserConstants.DETAILS);
      String email = details.get(UserConstants.EMAIL).asText();
      String password = details.get(UserConstants.PASSWORD).asText();
      String fp = request.get(UserConstants.FINGER_PRINT).asText();
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
              log.error(UserManagementLogs.TOKEN_GENERATION_FAILED + e.getMessage());
              return ShaftResponseBuilder.buildResponse(ShaftResponseCode.TOKEN_GENERATION_FAILED);
            }
            // #TODO Add retry spec if upsert failed with inbuilt method .retry(RetrySpec retrySpec)
            // Upsert FP to I identity if it's new fp for i
            return identityRepository.upsertFpAndIPair(user.getA(),fp,user.getI())
              .map(totalUpdated -> {
                if(totalUpdated <= 0) {
                  log.info("Failed to upsertFpAndIPair for account {} - user {}",user.getA(),user.getI());
                }
                ObjectNode response = interceptUserResponse(user,token);
                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.LOGIN_SUCCESS,response);
              })
              .onErrorResume(t -> {
                log.error(UserManagementLogs.FP_TO_I_UPSERT_FAILED + t.getMessage());
                return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.IDENTITY_UPDATE_FAILED));
              }).block();
          } else {
            return ShaftResponseBuilder.buildResponse(ShaftResponseCode.USER_NOT_FOUND);
          }
        })
        .onErrorResume(t-> {
          log.error(UserManagementLogs.FETCHING_USER_EXCEPTION,t);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.UNABLE_TO_FETCH_USER));
        });
    } else {
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BAD_LOGIN_REQUEST));
    }
  }

  public Mono<ObjectNode> registerUser(int account, ObjectNode request) {
    if (request.has(UserConstants.DETAILS) && request.has(UserConstants.FINGER_PRINT)) {
      JsonNode details = request.get(UserConstants.DETAILS);
      String email = details.get(UserConstants.EMAIL).asText();
      int newI = details.get(UserConstants.IDENTITY).asInt();
      String fp = request.get(UserConstants.FINGER_PRINT).asText();
      return authRepository.countByE(email)
        .collectList()
        .publishOn(Schedulers.boundedElastic())
        .flatMap(userList -> {
          if(!userList.isEmpty() && userList.get(0) > 0) {
            // User already exists
            return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.USER_EXISTS));
          } else {
            User user = new User();
            user.setI(newI);
            user.setC(Long.parseLong(details.get(UserConstants.CONTACT).asText()));
            user.setA(account);
            user.setNm("Tejas Birje");
            user.setE(email);
            user.setP(shaftHashing.transactPassword(Mode.ENCRYPT, details.get(UserConstants.PASSWORD).asText()));
            return authRepository.save(user)
              .publishOn(Schedulers.boundedElastic())
              .flatMap(user2 -> {
                Identity i = createIdentityObject(fp,newI);
                return identityRepository.save(account,i)
                  .flatMap(ide -> {
                    return accountRestClient.retrieveAccountMeta(account)
                      .publishOn(Schedulers.boundedElastic())
                      .mapNotNull(data -> {
                        ACCOUNT_ID.set(account);
                        Map<String,Object> meta;
                        String idx = "";
                        try {
                          meta = mapParser.readValue(data);
                          if(meta.containsKey(UserConstants.RESPONSE_CODE)
                            && ((String)meta.get(UserConstants.RESPONSE_CODE))
                            .startsWith(UserConstants.RESPONSE_CODE_INITIAL)) {
                            idx = (String) ((Map<String, Object>) meta.get(UserConstants.RESPONSE_DATA)).get(UserConstants.ACCOUNT_INDEX);
                          }
                        } catch (JsonProcessingException e) {
                          return ShaftResponseBuilder.buildResponse(
                            ShaftResponseCode.SHAFT_UNABLE_TO_RETRIEVE_ACCOUNT_META);
                        }
                        if(!idx.isEmpty()) {
                          EventIndex eventIndex = new EventIndex();
                          eventIndex.setEmail(email);
                          return eventIndexRepository.saveEventIndex(idx,eventIndex)
                            .map(evtIdx -> ShaftResponseBuilder.buildResponse(ShaftResponseCode.USER_REGISTERED,mapper.convertValue(ide, ObjectNode.class)))
                            .onErrorResume(error -> {
                              if(!(error instanceof RestStatusException)) {
                                log.error("Exception while creating document in event index");
                              }
                              return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.USER_REGISTERED,mapper.convertValue(ide, ObjectNode.class)));
                            }).block();
                        } else {
                          ACCOUNT_ID.remove();
                          log.error(UserManagementLogs.UNABLE_TO_FETCH_ACCOUNT_META);
                          return ShaftResponseBuilder.buildResponse(
                            ShaftResponseCode.SHAFT_UNABLE_TO_RETRIEVE_ACCOUNT_META);
                        }
                      })
                      .onErrorResume(t -> {
                        ACCOUNT_ID.remove();
                        log.error(UserManagementLogs.ERROR_IN_FETCHING_ACCOUNT_META,t);
                        return Mono.just(ShaftResponseBuilder.buildResponse(
                          ShaftResponseCode.ERROR_IN_FETCHING_ACCOUNT_META));
                      });
                  })
                  .onErrorResume(t -> {
                    if(isRestStatusException(t)) {
                      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.USER_REGISTERED,mapper.convertValue(i,ObjectNode.class)));
                    } else {
                      log.error(UserManagementLogs.AUTH_SAVE_IDENTITY_EXCEPTION,t.getMessage(),ACCOUNT_ID.get());
                      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_IDENTITY_REGISTRATION_ERROR));
                    }
                  });
              })
              .onErrorResume(t -> {
                if(isRestStatusException(t)) {
                  return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.USER_REGISTERED,mapper.convertValue(user,ObjectNode.class)));
                } else {
                  log.error(UserManagementLogs.SAVE_USER_EXCEPTION,t.getMessage(),ACCOUNT_ID.get());
                  return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_REGISTRATION_ERROR));
                }
              });
          }
        });
    }
    return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BAD_REGISTRATION_REQUEST));
  }

  private Identity createIdentityObject(String fp, int newI) {
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

  private boolean isRestStatusException(Throwable t) {
    return t instanceof RestStatusException;
  }
}
