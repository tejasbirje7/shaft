package org.shaft.administration.usermanagement.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.auth.utils.APIConstant;
import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.usermanagement.clients.AccountRestClient;
import org.shaft.administration.usermanagement.constants.API;
import org.shaft.administration.usermanagement.constants.Code;
import org.shaft.administration.usermanagement.constants.Log;
import org.shaft.administration.usermanagement.dao.IdentityDAO;
import org.shaft.administration.usermanagement.entity.Identity;
import org.shaft.administration.usermanagement.repositories.IdentityRepository;
import org.shaft.administration.usermanagement.util.ResponseBuilder;
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
  public IdentityService(IdentityRepository identityRepository, AccountRestClient accountRestClient) throws Exception {
    this.identityRepository = identityRepository;
    this.accountRestClient = accountRestClient;
    this.jwtUtil = new ShaftJWT();
    mapParser = new ObjectMapper().readerFor(Map.class);
    mapper = new ObjectMapper();
  }

  // #TODO Handle identity scenario with fingerprint as well as IP, because IP changes is not very frequent
  @Override
  public Mono<ObjectNode> checkIdentity(int account, Map<String,Object> details) {
    String fp = (String) details.get(API.FINGER_PRINT);

    // Check if `i` exists in request
    if (details.containsKey(API.IDENTITY) && !API.EMPTY.equals(details.get(API.IDENTITY))) {
      /*
       `i` exists in request so check
       if incoming fp is mapped with incoming `i`,
       if not then update fp
       */
      int i = Integer.parseInt((String) details.get(API.IDENTITY));
      boolean isIdentified = details.containsKey(API.IS_IDENTIFIED) && (boolean) details.get(API.IS_IDENTIFIED);
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
                .map(data -> {
                  ACCOUNT_ID.set(account);
                  Map<String,Object> meta;
                  String idx = "";
                  try {
                    meta = mapParser.readValue(data);
                    if(meta.containsKey(API.RESPONSE_CODE) && ((String)meta.get(API.RESPONSE_CODE)).startsWith(API.RESPONSE_CODE_INITIAL)) {
                      idx = (String) ((Map<String, Object>) meta.get(API.RESPONSE_DATA)).get(API.ACCOUNT_INDEX);
                    }
                  } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                  }
                  if(!idx.isEmpty()) {
                    Identity newFpToI = new Identity();
                    if (details.containsKey(API.REQUEST_TIME)) {
                      int newI = (Integer) details.get(API.REQUEST_TIME);
                      newFpToI.setIdentity(newI);
                      newFpToI.setIdentified(false);
                      Map<String,String> fpMapCreation = new HashMap<>();
                      fpMapCreation.put("g",fp);
                      List<Map<String,String>> fpArray = new ArrayList<>();
                      fpArray.add(fpMapCreation);
                      newFpToI.setFingerPrint(fpArray);
                      ACCOUNT_ID.set(account);
                      identityRepository.save(newFpToI)
                        .doOnSuccess(resp -> {
                          log.info("Saved successfully {}",resp);
                        })
                        .doOnError(error -> {
                          // #TODO Return exception here
                        }).subscribe();
                      response.put(API.IDENTITY,newI);
                      ACCOUNT_ID.remove();
                      return ResponseBuilder.buildResponse(Code.IDENTITY_FETCHED_SUCCESSFULLY,response);
                      // #TODO  Insert the event schema into `idx` index fetched above to track events
                    } else {
                      ACCOUNT_ID.remove();
                      log.error(Log.BAD_REQUEST_ACC_META);
                      return ResponseBuilder.buildResponse(Code.BAD_REQUEST_FOR_FETCHING_ACCOUNT_META);
                    }
                  } else {
                    ACCOUNT_ID.remove();
                    log.error(Log.UNABLE_TO_FETCH_ACCOUNT_META);
                    return ResponseBuilder.buildResponse(Code.SHAFT_UNABLE_TO_RETRIEVE_ACCOUNT_META);
                  }
                })
                .onErrorResume(t -> {
                  ACCOUNT_ID.remove();
                  log.error(Log.ERROR_IN_FETCHING_ACCOUNT_META,t);
                  return Mono.just(ResponseBuilder.buildResponse(Code.ERROR_IN_FETCHING_ACCOUNT_META));
                }).block();
            } else {
              // `i` exists return `fp`
              response.put(API.IDENTITY,fpDetails.get(0).getIdentity());
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
                response.put(API.IDENTITY,i);
                return ResponseBuilder.buildResponse(Code.IDENTITY_FETCHED_SUCCESSFULLY,response);
              } else {
                log.error(Log.FAILED_TO_UPDATE_FP);
                return ResponseBuilder.buildResponse(Code.SHAFT_FP_UPSERT_FAILED);
              }
            })
            .onErrorResume(t -> {
              if(!isRestStatusException(t)) {
                return Mono.just(ResponseBuilder.buildResponse(Code.SHAFT_FP_UPSERT_ERROR));
              } else {
                log.error(Log.ERROR_WHILE_UPDATE_FP,t);
                response.put(API.IDENTITY,i);
                ACCOUNT_ID.remove();
                return Mono.just(ResponseBuilder.buildResponse(Code.IDENTITY_FETCHED_SUCCESSFULLY,response));
              }
            }).block();
        } else {
          response.put(API.IDENTITY,i);
          ACCOUNT_ID.remove();
          return ResponseBuilder.buildResponse(Code.IDENTITY_FETCHED_SUCCESSFULLY,response);
        }
      })
      .onErrorResume(t -> {
        log.error(Log.FP_TO_I_MAPPING_FAILED,t);
        ACCOUNT_ID.remove();
        return Mono.just(ResponseBuilder.buildResponse(Code.SHAFT_FP_TO_I_MAPPING_FAILED));
      });
  }
}
