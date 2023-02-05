package com.shaft.administration.accountmanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shaft.administration.accountmanagement.constants.AccountConstants;
import com.shaft.administration.accountmanagement.constants.AccountLogs;
import com.shaft.administration.accountmanagement.dao.MetaDAO;
import com.shaft.administration.accountmanagement.entity.Meta;
import com.shaft.administration.accountmanagement.repositories.MetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
                String[] f = ((ArrayList<String>)fields.get(AccountConstants.FIELDS)).toArray(new String[0]);
                return metaRepository.getMetaFields(account, f)
                  .map(metaResponse -> {
                      ObjectNode n = mapper.convertValue(metaResponse,ObjectNode.class);
                      return ShaftResponseBuilder.buildResponse(ShaftResponseCode.META_FIELDS_FETCHED,n);
                  })
                  .onErrorResume(error -> {
                      log.error(AccountLogs.EXCEPTION_FETCHING_META_FIELDS,error,account);
                      return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_FETCHING_META_FIELDS));
                  });
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
}
