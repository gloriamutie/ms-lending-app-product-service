package com.glo.lending.product.repository.repo;

import com.glo.lending.product.repository.entities.ProductTenure;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Reactive repository for {@link ProductTenure} entities.
 */
@Repository
public interface ProductTenureRepository extends ReactiveCrudRepository<ProductTenure, UUID> {

    /**
     * Finds all tenure configurations for a given product.
     *
     * @param productId the product identifier
     * @return a {@link Flux} emitting matching tenure configurations
     */
    Flux<ProductTenure> findByProductId(UUID productId);
}

