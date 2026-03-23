package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * In-memory cache configuration using Caffeine.
 * Caches frequently read, rarely changed data (product lists, categories).
 *
 * NOTE: For multi-instance deployments (multiple pods) upgrade to Redis:
 *   spring.cache.type=redis
 *   + spring-boot-starter-data-redis dependency
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(500)
                // Products list: expire after 5 minutes
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
        );
        return manager;
    }
}
