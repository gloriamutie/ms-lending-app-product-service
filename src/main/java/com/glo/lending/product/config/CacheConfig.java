package com.glo.lending.product.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    public static final String CACHE_PRODUCTS = "products";
    public static final String CACHE_PRODUCT_FEES = "productFees";
    public static final String CACHE_PRODUCT_TENURES = "productTenures";

    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing CaffeineCacheManager with TTL");

        CaffeineCacheManager manager = new CaffeineCacheManager(
                CACHE_PRODUCTS,
                CACHE_PRODUCT_FEES,
                CACHE_PRODUCT_TENURES
        );

        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(10_000)
        );
        manager.registerCustomCache(CACHE_PRODUCTS,
                Caffeine.newBuilder()
                        .expireAfterWrite(60, TimeUnit.MINUTES)
                        .maximumSize(10_000)
                        .build()
        );

        manager.registerCustomCache(CACHE_PRODUCT_FEES,
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(10_000)
                        .build()
        );

        manager.registerCustomCache(CACHE_PRODUCT_TENURES,
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(10_000)
                        .build()
        );

        return manager;
    }
}