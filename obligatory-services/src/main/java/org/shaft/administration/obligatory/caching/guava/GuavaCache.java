package org.shaft.administration.obligatory.caching.guava;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class GuavaCache<T> {
    private Cache<String, T> cache;

    //Constructor to build Cache Store
    public GuavaCache(int expiryDuration, TimeUnit timeUnit) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiryDuration, timeUnit)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    //Method to fetch previously stored record using record key
    public T get(String key) {
        return cache.getIfPresent(key);
    }

    //Method to put a new record in Cache Store with record key
    public void add(String key, T value) {
        if(key != null && value != null) {
            cache.put(key, value);
            System.out.println("Record stored in "
                    + value.getClass().getSimpleName()
                    + " Cache with Key = " + key);
        }
    }
}
