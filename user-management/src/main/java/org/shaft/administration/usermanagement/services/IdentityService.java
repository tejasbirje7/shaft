package org.shaft.administration.usermanagement.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.usermanagement.clients.AccountRestClient;
import org.shaft.administration.usermanagement.constants.UserManagementConstants;
import org.shaft.administration.usermanagement.constants.UserManagementLogs;
import org.shaft.administration.usermanagement.dao.IdentityDAO;
import org.shaft.administration.usermanagement.entity.Identity;
import org.shaft.administration.usermanagement.repositories.IdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IdentityService implements IdentityDAO {
  private final IdentityRepository identityRepository;
  private final AccountRestClient accountRestClient;
  private final ObjectReader mapParser;
  private final ShaftJWT jwtUtil;
  private final ObjectMapper mapper;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }

  @Autowired
  public IdentityService(IdentityRepository identityRepository,
                         AccountRestClient accountRestClient) throws Exception {
    this.mapper = new ObjectMapper();
    this.jwtUtil = new ShaftJWT();
    this.accountRestClient = accountRestClient;
    this.identityRepository = identityRepository;
    this.mapParser = new ObjectMapper().readerFor(Map.class);
  }

  // #TODO Handle identity scenario with fingerprint as well as IP, because IP changes is not very frequent
  @Override
  public Mono<ObjectNode> checkIdentity(int account, Map<String,Object> details) {
    String fp = (String) details.get(UserManagementConstants.FINGER_PRINT);

    // Check if `i` exists in request
    if (details.containsKey(UserManagementConstants.IDENTITY)
      && !UserManagementConstants.EMPTY.equals(details.get(UserManagementConstants.IDENTITY))) {
      /*
       `i` exists in request so check
       if incoming fp is mapped with incoming `i`,
       if not then update fp
       */
      int i = Integer.parseInt((String) details.get(UserManagementConstants.IDENTITY));
      boolean isIdentified = details.containsKey(UserManagementConstants.IS_IDENTIFIED)
        && (boolean) details.get(UserManagementConstants.IS_IDENTIFIED);
      return checkIfFpExistsForI(fp,i,isIdentified,account);
    } else {
      // `i` doesn't exist in request
      return identityRepository.checkIfIExistsForFp(fp)
        .collectList()
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(fpDetails -> {
            ACCOUNT_ID.set(account);
            ObjectNode response = mapper.createObjectNode();
            if(fpDetails.isEmpty()) {
              return accountRestClient.retrieveAccountMeta(account)
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(data -> {
                  ACCOUNT_ID.set(account);
                  Map<String,Object> meta;
                  String idx = "";
                  try {
                    meta = mapParser.readValue(data);
                    if(meta.containsKey(UserManagementConstants.RESPONSE_CODE)
                      && ((String)meta.get(UserManagementConstants.RESPONSE_CODE))
                      .startsWith(UserManagementConstants.RESPONSE_CODE_INITIAL)) {
                      idx = (String) ((Map<String, Object>) meta
                        .get(UserManagementConstants.RESPONSE_DATA))
                        .get(UserManagementConstants.ACCOUNT_INDEX);
                    }
                  } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                  }
                  if(!idx.isEmpty()) {
                    Identity newFpToI = new Identity();
                    if (details.containsKey(UserManagementConstants.REQUEST_TIME)) {
                      int newI = (Integer) details.get(UserManagementConstants.REQUEST_TIME);
                      newFpToI.setIdentity(newI);
                      newFpToI.setIdentified(false);
                      Map<String,String> fpMapCreation = new HashMap<>();
                      fpMapCreation.put("g",fp);
                      List<Map<String,String>> fpArray = new ArrayList<>();
                      fpArray.add(fpMapCreation);
                      newFpToI.setFingerPrint(fpArray);
                      ACCOUNT_ID.set(account);
                      return identityRepository.save(newFpToI)
                        .map(resp -> {
                          log.info(UserManagementLogs.IDENTITY_SAVED_SUCCESSFULLY,resp);
                          response.put(UserManagementConstants.IDENTITY,newI);
                          ACCOUNT_ID.remove();
                          return ShaftResponseBuilder.buildResponse(
                            ShaftResponseCode.IDENTITY_FETCHED_SUCCESSFULLY,response);
                        })
                        .onErrorResume(error -> {
                          if(isRestStatusException(error)) {
                            response.put(UserManagementConstants.IDENTITY,newI);
                            return Mono.just(ShaftResponseBuilder.buildResponse(
                              ShaftResponseCode.IDENTITY_FETCHED_SUCCESSFULLY, response));
                          }
                          ACCOUNT_ID.remove();
                          log.error(UserManagementLogs.IDENTITY_SAVE_EXCEPTION,error,ACCOUNT_ID.get());
                          return Mono.just(ShaftResponseBuilder.buildResponse(
                            ShaftResponseCode.SHAFT_FP_TO_I_FAILED));
                        }).block();
                      // #TODO  Insert the event schema into `idx` index fetched above to track events
                    } else {
                      ACCOUNT_ID.remove();
                      log.error(UserManagementLogs.BAD_REQUEST_ACC_META);
                      return ShaftResponseBuilder.buildResponse(
                        ShaftResponseCode.BAD_REQUEST_FOR_FETCHING_ACCOUNT_META);
                    }
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
                }).block();
            } else {
              // `i` exists return `fp`
              response.put(UserManagementConstants.IDENTITY,fpDetails.get(0).getIdentity());
            }
            ACCOUNT_ID.remove();
            return response;
          }
        );
    }
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
      if(tokenDetails.containsKey(UserManagementConstants.IDENTITY)) {
        i.put(UserManagementConstants.IDENTITY,
          (Integer) tokenDetails.get(UserManagementConstants.IDENTITY));
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
  private boolean isRestStatusException(Throwable t) {
    return t instanceof RestStatusException;
  }

  private Mono<ObjectNode> checkIfFpExistsForI(String fp,int i,boolean isIdentified,int account) {
    ACCOUNT_ID.set(account);
    // Check if fp exists for `i`
    return identityRepository.checkIfFpExistsForI(fp,i,isIdentified)
      .collectList()
      .publishOn(Schedulers.boundedElastic())
      .mapNotNull(fpDetails -> {
        ObjectNode response = mapper.createObjectNode();
        if (fpDetails.isEmpty()) {
          ACCOUNT_ID.set(account);
          // fp doesn't exist, so update this fp for incoming `i`
          return identityRepository.updateFp(fp,i)
            .map(totalUpdated -> {
              if(totalUpdated > 0) {
                ACCOUNT_ID.remove();
                response.put(UserManagementConstants.IDENTITY,i);
                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.IDENTITY_FETCHED_SUCCESSFULLY,response);
              } else {
                log.error(UserManagementLogs.FAILED_TO_UPDATE_FP);
                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_FP_UPSERT_FAILED);
              }
            })
            .onErrorResume(t -> {
              if(!isRestStatusException(t)) {
                return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_FP_UPSERT_ERROR));
              } else {
                log.error(UserManagementLogs.ERROR_WHILE_UPDATE_FP,t);
                response.put(UserManagementConstants.IDENTITY,i);
                ACCOUNT_ID.remove();
                return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.IDENTITY_FETCHED_SUCCESSFULLY,response));
              }
            }).block();
        } else {
          response.put(UserManagementConstants.IDENTITY,i);
          ACCOUNT_ID.remove();
          return ShaftResponseBuilder.buildResponse(ShaftResponseCode.IDENTITY_FETCHED_SUCCESSFULLY,response);
        }
      })
      .onErrorResume(t -> {
        log.error(UserManagementLogs.FP_TO_I_MAPPING_FAILED,t);
        ACCOUNT_ID.remove();
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_FP_TO_I_MAPPING_FAILED));
      });
  }
}
