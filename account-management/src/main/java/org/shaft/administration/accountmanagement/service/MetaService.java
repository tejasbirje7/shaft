package org.shaft.administration.accountmanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.accountmanagement.constants.AccountConstants;
import org.shaft.administration.accountmanagement.constants.AccountLogs;
import org.shaft.administration.accountmanagement.dao.MetaDAO;
import org.shaft.administration.accountmanagement.entity.EventAndPropsMeta;
import org.shaft.administration.accountmanagement.entity.EventsMeta;
import org.shaft.administration.accountmanagement.entity.PropsMeta;
import org.shaft.administration.accountmanagement.repositories.MetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MetaService implements MetaDAO {
  MetaRepository metaRepository;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  ObjectMapper mapper = new ObjectMapper();
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }

  @Autowired
  public MetaService(MetaRepository metaRepository) {
    this.metaRepository = metaRepository;
  }

  @Override
  public Mono<ObjectNode> getMetaFields(int account, Map<String, Object> fields) {
    try {
      if(!fields.isEmpty()) {
        ACCOUNT_ID.set(account);
        if(!fields.isEmpty()) {
          String[] f = ((ArrayList<String>)fields.get(AccountConstants.FIELDS)).toArray(new String[0]);
          return metaRepository.getMetaFields(account,f)
            .map(metaResponse -> {
              ObjectNode n = mapper.convertValue(metaResponse,ObjectNode.class);
              return ShaftResponseBuilder.buildResponse(ShaftResponseCode.META_FIELDS_FETCHED,n);
            })
            .onErrorResume(error -> {
              log.error(AccountLogs.EXCEPTION_FETCHING_META_FIELDS,error,account);
              return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_FETCHING_META_FIELDS));
            });
        } else {
          return metaRepository.getMetaFields(account)
            .map(metaResponse -> {
              ObjectNode n = mapper.convertValue(metaResponse,ObjectNode.class);
              return ShaftResponseBuilder.buildResponse(ShaftResponseCode.META_FIELDS_FETCHED,n);
            })
            .onErrorResume(error -> {
              log.error(AccountLogs.EXCEPTION_FETCHING_META_FIELDS,error,account);
              return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_FETCHING_META_FIELDS));
            });
        }
      } else {
        log.error(AccountLogs.BAD_META_REQUEST,account);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.BAD_META_REQUEST));
      }
    } catch (Exception ex) {
      log.error(AccountLogs.UNABLE_TO_CONSTRUCT_META_FIELDS,ex,account);
      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_CONSTRUCTING_META_FIELDS));
    } finally {
      ACCOUNT_ID.remove();
    }
  }

  @Override
  public Mono<ObjectNode> getEventsMeta(int account) {
    Mono<List<EventsMeta>> eventsMeta = metaRepository.getEventsMeta(account).collectList();
    Mono<List<PropsMeta>> propsMeta = metaRepository.getPropsMeta(account).collectList();
    EventAndPropsMeta eventPropsMeta = new EventAndPropsMeta();

    return Mono.zip(eventsMeta,propsMeta)
      .map(dMono -> {
        eventPropsMeta.setEventsMeta(dMono.getT1());
        eventPropsMeta.setPropsMeta(dMono.getT2());
        return ShaftResponseBuilder.buildResponse(
          ShaftResponseCode.EVENTS_META_RETRIEVED,mapper.valueToTree(eventPropsMeta));
      })
      .onErrorResume(error -> Mono.just(ShaftResponseBuilder.buildResponse(
        ShaftResponseCode.FAILED_TO_FETCH_EVENTS_META)));
  }
}
