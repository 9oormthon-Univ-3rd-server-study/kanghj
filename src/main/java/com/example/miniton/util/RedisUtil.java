//package com.example.miniton.util;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//
//@Component
//@RequiredArgsConstructor
//public class RedisUtil {
//    // Redis 에 저장할 값과 키가 모두 문자열일 때 사용
//    private final StringRedisTemplate stringRedisTemplate;
//
//    public String getData(String key){
//        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
//        return valueOperations.get(key);
//    }
//
//    public void setData(String key, String value){
//        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
//        valueOperations.set(key,value);
//    }
//
//    public void setDataExpire(String key, String value, long duration){
//        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
//        Duration expireDuration = Duration.ofSeconds(duration);
//        valueOperations.set(key, value, expireDuration);
//    }
//
//    public void deleteData(String key){
//        stringRedisTemplate.delete(key);
//    }
//}
