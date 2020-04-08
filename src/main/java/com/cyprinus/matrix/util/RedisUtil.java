package com.cyprinus.matrix.util;

import com.cyprinus.matrix.type.MatrixRedisPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
@Component
public class RedisUtil {

    public RedisTemplate getRedis() {
        return redis;
    }

    private final
    ObjectUtil objectUtil;

    private final
    StringRedisTemplate redis;

    private ValueOperations<String, String> operations;

    @Autowired
    public RedisUtil(StringRedisTemplate redisTemplate, ObjectUtil objectUtil) {
        this.redis = redisTemplate;
        operations = redisTemplate.opsForValue();
        this.objectUtil = objectUtil;
    }

    public void set(String key, Object value) throws JsonProcessingException {
        String json = objectUtil.obj2json(value);
        operations.set(key, json);
    }

    public void set(String key, Object value, long duration, TimeUnit timeUnit) throws JsonProcessingException {
        String json = objectUtil.obj2json(value);
        operations.set(key, json, duration, timeUnit);
    }

    public <T> T get(String key, Class<T> classType) throws JsonProcessingException {
        return  objectUtil.json2obj(operations.get(key), classType);
    }

    public MatrixRedisPayload get(String key) throws JsonProcessingException {
        return objectUtil.json2obj(operations.get(key), MatrixRedisPayload.class);
    }

    Boolean hasKey(String key) {
        return redis.hasKey(key);
    }

}
