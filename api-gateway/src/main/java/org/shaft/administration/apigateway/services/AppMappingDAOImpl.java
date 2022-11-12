package org.shaft.administration.apigateway.services;

import org.shaft.administration.apigateway.common.CacheStore;
import org.shaft.administration.apigateway.dao.AppMappingDAO;
import org.shaft.administration.apigateway.entity.Mappings.AppMapping;
import org.shaft.administration.apigateway.repositories.AppGatewayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class AppMappingDAOImpl implements AppMappingDAO {
    private AppGatewayRepository appMappingRepo;
    private AppMapping mappings;
    private CacheStore<AppMapping> mappingCache = new CacheStore<AppMapping>(86400, TimeUnit.SECONDS);
    private static final String CACHE_KEY = "appMappings";
    @Autowired
    public void setAppMappingRepo(AppGatewayRepository appMappingRepo) {
        this.appMappingRepo = appMappingRepo;
    }
    @Override
    public AppMapping getMappings() {
        Object m = mappingCache.get(CACHE_KEY);
        if (m != null) {
            mappings = (AppMapping) m;
        } else {
            Iterable i = appMappingRepo.findAll();
            mappings = (AppMapping) i.iterator().next();
            mappingCache.add(CACHE_KEY,mappings);
        }
        return mappings;
    }
}
