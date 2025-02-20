package com.jkrc.repositoryscore.configuration.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager getCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache("repositoryScores",
                Caffeine.newBuilder()
                        .recordStats()
                        .expireAfterWrite(Duration.ofMinutes(20))
                        .maximumSize(500)
                        .build()
        );
        cacheManager.registerCustomCache("scoredRepositories",
                Caffeine.newBuilder()
                        .recordStats()
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .maximumSize(500)
                        .build()
        );
        return cacheManager;
    }
}
