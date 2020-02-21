package com.techdevsolutions.cache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    private Cache<String, String> cache = null;

    @Autowired
    public CacheService(Environment environment) {
        Boolean expire = environment.getProperty("cache.expire.enabled") != null
                ? Boolean.valueOf(environment.getProperty("cache.expire.enabled")) :
                false;
        Integer expireMin = environment.getProperty("cache.expire.time.minutes") != null
                ? Integer.valueOf(environment.getProperty("cache.expire.time.minutes")) :
                10;
        Integer maxSize = environment.getProperty("cache.size.max") != null
                ? Integer.valueOf(environment.getProperty("cache.size.max")) :
                1000;

        Caffeine<Object, Object> i = Caffeine.newBuilder();

        if (expire) {
            i.expireAfterWrite(expireMin, TimeUnit.MINUTES);
        }

//        i.maximumWeight(100000);
//        i.weigher(new Weigher<String, String>() {
//            public int weigh(String key, String value) {
//                return value.length();
//            }
//        });

        i.maximumSize(maxSize);
        this.cache = i.build();
    }

    public String get(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            throw new Exception("key is null or empty");
        }

        return this.cache.getIfPresent(key);
    }
    public void set(String key, String value) throws Exception {
        if (StringUtils.isEmpty(key)) {
            throw new Exception("key is null or empty");
        }

        this.cache.put(key, value);
    }
}
