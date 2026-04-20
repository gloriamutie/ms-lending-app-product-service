package com.glo.lending.product.repository.repo;

import com.glo.lending.product.repository.entities.ProductFee;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Reactive repository for {@link ProductFee} entities.
 */
@Repository
public interface ProductFeeRepository extends ReactiveCrudRepository<ProductFee, UUID> {

    /**
     * Finds all fee configurations for a given product.
     *
     * @param productId the product identifier
     * @return a {@link Flux} emitting matching fee configurations
     */
    Flux<ProductFee> findByProductId(UUID productId);
}

