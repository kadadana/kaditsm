package com.kadadana.kaditsm.modules.session.entity;

//JAVA IMPORTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("blacklisted_tokens")
public class BlacklistedToken {

    @Id
    private String id;

    private String userId;

    @TimeToLive
    private long expiration;
}