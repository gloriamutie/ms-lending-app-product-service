package com.glo.lending.product.components;

import com.glo.lending.product.config.CacheConfig;
import com.glo.lending.product.dblayer.entities.Product;
import com.glo.lending.product.dblayer.entities.ProductFee;
import com.glo.lending.product.dblayer.entities.ProductTenure;
import com.glo.lending.product.dblayer.repo.ProductFeeRepository;
import com.glo.lending.product.dblayer.repo.ProductRepository;
import com.glo.lending.product.dblayer.repo.ProductTenureRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ProductCache {

    private static final Logger log = LoggerFactory.getLogger(ProductCache.class);

    private final ProductRepository productRepository;
    private final ProductFeeRepository productFeeRepository;
    private final ProductTenureRepository productTenureRepository;
    private final CacheManager cacheManager;

    public Mono<Product> getProductById(final UUID productId) {
        final Cache cache = cacheManager.getCache(CacheConfig.CACHE_PRODUCTS);
        if (cache != null) {
            final Product cachedProduct = cache.get(productId, Product.class);
            if (cachedProduct != null) {
                log.debug("Product returned from cache: {}", productId);
                return Mono.just(cachedProduct);
            }
        }
        log.debug("Product not in cache: {}", productId);
        return productRepository.findById(productId).doOnNext(product -> {
                    if (cache != null) {
                        cache.put(productId, product);
                    }
                });
    }


    @SuppressWarnings("unchecked")
    public Flux<ProductFee> getFeesByProductId(final UUID productId) {
        final Cache cache = cacheManager.getCache(CacheConfig.CACHE_PRODUCT_FEES);
        if (cache != null) {
            final Cache.ValueWrapper wrapper = cache.get(productId);
            if (wrapper != null) {
                log.debug("Product fees returned from cache: {}", productId);
                return Flux.fromIterable((List<ProductFee>) wrapper.get());
            }
        }
        log.debug("Product fees not in cache: {}", productId);
        return productFeeRepository.findByProductId(productId)
                .collectList()
                .doOnNext(fees -> {
                    if (cache != null) {
                        cache.put(productId, fees);
                    }
                    log.debug("Cached {} fees for product: {}", fees.size(), productId);
                })
                .flatMapMany(Flux::fromIterable);
    }


    @SuppressWarnings("unchecked")
    public Flux<ProductTenure> getTenuresByProductId(UUID productId) {

        Cache cache = cacheManager.getCache(CacheConfig.CACHE_PRODUCT_TENURES);

        if (cache != null) {
            List<ProductTenure> cachedProductTenure = cache.get(productId, List.class);

            if (cachedProductTenure != null) {
                log.debug("Product tenures returned from cache: {}", productId);
                return Flux.fromIterable(cachedProductTenure);
            }
        }
        log.debug("No product tenures cached: {}", productId);
        return productTenureRepository.findByProductId(productId)
                .collectList()
                .doOnNext(tenures -> {
                    if (cache != null) {
                        cache.put(productId, tenures);
                    }
                    log.debug("Cached {} tenures for product: {}", tenures.size(), productId);
                })
                .flatMapMany(Flux::fromIterable);
    }

 //Evicts all cache entries for a specific product.
 // Called after any update to product, fees, or tenures to ensure cache consistency.
    public void evictProduct( UUID productId) {
        log.info("Evicting cache entries for product: {}", productId);
        evictFromCache(CacheConfig.CACHE_PRODUCTS, productId);
        evictFromCache(CacheConfig.CACHE_PRODUCT_FEES, productId);
        evictFromCache(CacheConfig.CACHE_PRODUCT_TENURES, productId);
    }


     // Evicts all entries from all product caches

    public void evictAll() {
        log.info("Evicting all product cache entries");
        clearCache(CacheConfig.CACHE_PRODUCTS);
        clearCache(CacheConfig.CACHE_PRODUCT_FEES);
        clearCache(CacheConfig.CACHE_PRODUCT_TENURES);
    }

    private void evictFromCache( String cacheName,  UUID key) {
        final Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    private void clearCache( String cacheName) {
        final Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}
