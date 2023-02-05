package com.shaft.administration.accountmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shaft.administration.accountmanagement.constants.AccountConstants;
import com.shaft.administration.accountmanagement.constants.AccountLogs;
import com.shaft.administration.accountmanagement.dao.DashboardDAO;
import com.shaft.administration.accountmanagement.repositories.MetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base64;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Slf4j
@Service
public class DashboardService implements DashboardDAO {

    MetaRepository metaRepository;
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }
    ObjectMapper mapper;
    ShaftQueryTranslator queryTranslator;

    @Autowired
    public DashboardService(MetaRepository metaRepository) {
        this.mapper = new ObjectMapper();
        this.metaRepository = metaRepository;
        this.queryTranslator = new ShaftQueryTranslator();
    }

    // #TODO Need to check this service. Not checking now since request payload is not available in hand which is complex too
    @Override
    public Mono<ObjectNode> pinToDashboard(int accountId, Map<String,Object> rawQuery) {
        ACCOUNT_ID.set(accountId);
        return metaRepository.getMetaFields(accountId,new String[]{AccountConstants.DASHBOARD_QUERIES})
          .publishOn(Schedulers.boundedElastic())
          .flatMap(meta -> {
              // Introduced limit here since on app launch app should not wait to load more query results
              if(meta.getDashboardQueries().size() > 5) {
                  return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.DASHBOARD_QUERIES_LIMIT_EXCEEDED));
              } else {
                  try {
                      ObjectNode rawQry = mapper.convertValue(rawQuery,ObjectNode.class);
                      ObjectNode query = this.queryTranslator.translateToElasticQuery(rawQry,true);
                      ACCOUNT_ID.set(accountId);
                      String q = mapper.writeValueAsString(query);
                      byte[] bytesEncoded = Base64.encodeBase64(q.getBytes());
                      return metaRepository.pinToDashboard(accountId,new String(bytesEncoded))
                        .map(totalPinned -> {
                            ObjectNode constructResponse = mapper.createObjectNode();
                            if(totalPinned > 0) {
                                constructResponse.put(AccountConstants.UPDATED,true);
                                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.DASHBOARD_QUERY_PINNED,constructResponse);
                            } else {
                                log.error(AccountLogs.FAILED_TO_PINNED_DASHBOARD_QUERY,accountId);
                                constructResponse.put(AccountConstants.UPDATED,false);
                                return ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_PINNED_DASHBOARD_QUERY,constructResponse);
                            }
                        })
                        .onErrorResume(error -> {
                            log.error(AccountLogs.EXCEPTION_PINNING_DASHBOARD_QUERY,accountId);
                            return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_PINNING_DASHBOARD_QUERY));
                        });
                  } catch (JsonProcessingException e) {
                      log.error(AccountLogs.INVALID_PIN_TO_DASHBOARD_REQUEST,accountId);
                      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.INVALID_PIN_TO_DASHBOARD_REQUEST));
                  } catch (Exception ex) {
                      log.error(AccountLogs.EXCEPTION_CONSTRUCTING_PIN_TO_DASHBOARD_QUERY,accountId);
                      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_CONSTRUCTING_PIN_TO_DASHBOARD_QUERY));
                  } finally {
                      ACCOUNT_ID.remove();
                  }
              }
          })
          .onErrorResume(error -> {
              log.error(AccountLogs.ERROR_FETCHING_META_FIELDS,accountId);
              return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_FETCHING_META_FIELDS));
          });
    }
}
