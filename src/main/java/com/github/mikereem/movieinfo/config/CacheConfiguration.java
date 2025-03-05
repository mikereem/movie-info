package com.github.mikereem.movieinfo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String MOVIE_CACHE_NAME = "movieCache";
    public static final String OMDB_SEARCH_CACHE_NAME = "omdbSearchCache";
    public static final String TMDB_SEARCH_CACHE_NAME = "tmdbSearchCache";
    public static final String OMDB_DIRECTORS_CACHE_NAME = "omdbDirectorsCache";
    public static final String TMDB_DIRECTORS_CACHE_NAME = "tmdbDirectorsCache";

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put(MOVIE_CACHE_NAME, defaultConfig);
        configs.put(OMDB_SEARCH_CACHE_NAME, defaultConfig);
        configs.put(TMDB_SEARCH_CACHE_NAME, defaultConfig);
        configs.put(OMDB_DIRECTORS_CACHE_NAME, defaultConfig.entryTtl(Duration.ofHours(1)));
        configs.put(TMDB_DIRECTORS_CACHE_NAME, defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .withInitialCacheConfigurations(configs)
                .cacheDefaults(defaultConfig)
                .build();
    }
}
