package com.shaft.administration.accountmanagement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaft.administration.accountmanagement.dao.MetaDAO;
import com.shaft.administration.accountmanagement.entity.Meta;
import com.shaft.administration.accountmanagement.repositories.MetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetaDAOImpl implements MetaDAO {
    MetaRepository metaRepository;
    ObjectMapper mapper = new ObjectMapper();
    public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);

    public static int getAccount() {
        return ACCOUNT_ID.get();
    }

    @Autowired
    public MetaDAOImpl(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
    }

    @Override
    public Map<String, Object> getMetaFields(int account,Map<String, Object> fields) {
        ACCOUNT_ID.set(account);
        Map<String,Object> response = new HashMap<>();
        String[] f = ((ArrayList<String>)fields.get("fields")).toArray(new String[0]);
        try {
            Meta m = metaRepository.getMetaFields(account,f);
            response = mapper.convertValue(m, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            System.out.println("Error while retrieving meta fields");
        }
        return response;
    }

    /**
     * ObjectMapper mapper = new ObjectMapper();
     *
     * // Convert POJO to Map
     * Map<String, Object> map =
     *     mapper.convertValue(foo, new TypeReference<Map<String, Object>>() {});
     *
     * // Convert Map to POJO
     * Foo anotherFoo = mapper.convertValue(map, Foo.class);
     */
}
