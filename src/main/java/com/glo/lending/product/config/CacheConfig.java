package com.glo.lending.product.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    public static final String CACHE_PRODUCTS = "products";
    public static final String CACHE_PRODUCT_FEES = "productFees";
    public static final String CACHE_PRODUCT_TENURES = "productTenures";


    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing Spring ConcurrentMapCacheManager with caches: {}, {}, {}",
                CACHE_PRODUCTS, CACHE_PRODUCT_FEES, CACHE_PRODUCT_TENURES);
        return new ConcurrentMapCacheManager(CACHE_PRODUCTS, CACHE_PRODUCT_FEES, CACHE_PRODUCT_TENURES);
    }
}
