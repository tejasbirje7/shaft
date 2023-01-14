package com.shaft.administration.accountmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shaft.administration.accountmanagement.dao.DashboardDAO;
import com.shaft.administration.accountmanagement.repositories.MetaRepository;
import org.shaft.administration.obligatory.translator.elastic.ShaftQueryTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base64;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

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
        this.metaRepository = metaRepository;
        this.mapper = new ObjectMapper();
        this.queryTranslator = new ShaftQueryTranslator();
    }

    // #TODO Need to check this method. Not checking now since request payload is not available in hand which is complex too
    @Override
    public Mono<Boolean> pinToDashboard(int accountId, Map<String,Object> rawQuery) {
        ACCOUNT_ID.set(accountId);
        return metaRepository.getMetaFields(accountId,new String[]{"dashboardQueries"})
          .mapNotNull(meta -> {
              // Introduced limit here since on app launch app should not wait to load more query results
              if(meta.getDashboardQueries().size() > 5) {
                  // #TODO Throw limit exceeded exception
                  return null;
              } else {
                  return meta;
              }
          })
          .map(meta -> {
              ObjectNode rawQry = mapper.convertValue(rawQuery,ObjectNode.class);
              ObjectNode query = this.queryTranslator.translateToElasticQuery(rawQry,true);
              ACCOUNT_ID.set(accountId);
              try {
                  String q = mapper.writeValueAsString(query);
                  byte[] bytesEncoded = Base64.encodeBase64(q.getBytes());
                  return metaRepository.pinToDashboard(accountId,new String(bytesEncoded));
              } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);
              } catch (Exception ex) {
                  throw  new RuntimeException(ex.getMessage());
              } finally {
                  ACCOUNT_ID.remove();
              }
          })
          .publishOn(Schedulers.boundedElastic())
          .map(updatedCount -> updatedCount.map(y -> y > 0))
          .hasElement(); // #TODO check if we can remove dependency of .publishOn(Schedulers.boundedElastic()) & .map
    }
}
