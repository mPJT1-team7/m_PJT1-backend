package com.example.miniproj.common.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    /*
    Redis 자료구조     ------------     Spring Redis API 
    String                             opsForValue()
    List                               opsForList()

    */
    private final RedisTemplate<String, Object> redisTemplate ;

    private static final long REFRESH_TOKEN_TTL =  60 * 60 * 24 * 7 ; // 7일
    
    public void saveToken(String email, String refreshToken) {
        System.out.println(">>>> RefreshTokenService save token");
        redisTemplate.opsForValue()
            .set("RT:"+email, refreshToken, REFRESH_TOKEN_TTL, TimeUnit.SECONDS); 
    }

    public void deleteToken(String email) {
        System.out.println(">>>> RefreshTokenService delete token");
        redisTemplate.delete("RT:"+email);
    }

    public String findByEmail(String email) {
        System.out.println(">>>> RefreshTokenService findByEmail");
        return (String)redisTemplate.opsForValue().get("RT:"+email) ;
    }

}
