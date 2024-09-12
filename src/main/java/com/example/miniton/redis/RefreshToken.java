package com.example.miniton.redis;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 3600) // Redis에 저장될 Domain Object를 Redis Hash 자료구조로 변환하는 방식
public class RefreshToken {
    private String refreshToken;

    @Id
    @Indexed
    private Long id;

    public RefreshToken(Long id, String refreshToken){
        this.id = id;
        this.refreshToken = refreshToken;
    }
}
