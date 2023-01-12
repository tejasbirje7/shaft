package com.shaft.administration.accountmanagement.service;

import com.shaft.administration.accountmanagement.dao.MetaDAO;
import com.shaft.administration.accountmanagement.entity.Meta;
import com.shaft.administration.accountmanagement.repositories.MetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

@Service
public class MetaDAOImpl implements MetaDAO {
    MetaRepository metaRepository;
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public MetaDAOImpl(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
    }

    @Override
    public Mono<Meta> getMetaFields(int account,Map<String, Object> fields) {
        ACCOUNT_ID.set(account);
        String[] f = ((ArrayList<String>)fields.get("fields")).toArray(new String[0]);
        try {
            return metaRepository.getMetaFields(account, f).doOnSuccess(Mono::just);
        } catch (Exception ex) {
            System.out.println("Error while retrieving meta fields");
        } finally {
            ACCOUNT_ID.remove();
        }
        return Mono.empty();
    }
}
