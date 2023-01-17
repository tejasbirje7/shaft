package org.shaft.administration.usermanagement.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.shaft.administration.usermanagement.clients.AccountRestClient;
import org.shaft.administration.usermanagement.dao.IdentityDAO;
import org.shaft.administration.usermanagement.entity.Identity;
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
public class IdentityService implements IdentityDAO {
  private final IdentityRepository identityRepository;
  private final AccountRestClient accountRestClient;
  private final ObjectReader mapParser;
  private final ShaftJWT jwtUtil;
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
  }

  // #TODO Handle identity scenario with fingerprint as well as IP, because IP changes is not very frequent
  @Override
  public Mono<Object> checkIdentity(int account, Map<String,Object> details) {
    ACCOUNT_ID.set(account);
    // #TODO Handle all exceptions and provide different response code for each exception
    String fp = (String) details.get("fp");
    // Check if `i` exists in request
    if (details.containsKey("i") && !"".equals(details.get("i"))) {
      int i = Integer.parseInt((String) details.get("i"));
      boolean isIdentified = details.containsKey("isIdentified") && (boolean) details.get("isIdentified");

      Mono<List<Identity>> doesFpExists = identityRepository.checkIfFpExistsForI(fp,i,isIdentified).collectList();
      Mono<Long> ifFpEmptyUpdateFpToI = identityRepository.updateFp(fp,i);

      // #TODO Return Mono<Map<String,Integer>> instead of Mono<Object>. Most probably will require one more .map
      return Mono.from(doesFpExists)
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(fpDetails -> {
          Map<String,Integer> response = new HashMap<>();
          if (fpDetails.isEmpty()) {
            return ifFpEmptyUpdateFpToI.map(totalUpdated -> {
              int success = totalUpdated > 0 ? i : -1;
              response.put("i",success);
              return response;
            }).block();
          } else {
            response.put("i",i);
            return response;
          }
        });
    } else {
      // `i` doesn't exist in request
      Mono<List<Identity>> doesIExistsForFp = identityRepository.checkIfIExistsForFp(fp).collectList();
      Mono<String> retrieveAccountMeta = accountRestClient.retrieveAccountMeta(account);

      return doesIExistsForFp
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(fpDetails -> {
            ACCOUNT_ID.set(account);
            Map<String,Integer> response = new HashMap<>();
            if(fpDetails.isEmpty()) {
              return retrieveAccountMeta
                .publishOn(Schedulers.boundedElastic())
                .map(data -> {
                  ACCOUNT_ID.set(account);
                  Map<String,Object> meta;
                  String idx = "";
                  try {
                    meta = mapParser.readValue(data);
                    if(meta.containsKey("code") && ((String)meta.get("code")).startsWith("S")) {
                      idx = (String) ((Map<String, Object>) meta.get("data")).get("idx");
                    }
                  } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                  }
                  if(!idx.isEmpty()) {
                    Identity newFpToI = new Identity();
                    if (details.containsKey("rt")) {
                      int newI = (Integer) details.get("rt");
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
                      response.put("i",newI);
                      ACCOUNT_ID.remove();
                      return response;
                      // #TODO  Insert the event schema into `idx` index fetched above to track events
                    } else {
                      // #TODO Raise Exception BAD REQUEST
                    }
                  } else {
                    // #TODO Raise Exception ACCOUNT SERVICE DOWN
                  }
                  ACCOUNT_ID.remove();
                  return response;
                }).block();
            } else {
              // `i` exists return `fp`
              response.put("i",fpDetails.get(0).getIdentity());
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
}
