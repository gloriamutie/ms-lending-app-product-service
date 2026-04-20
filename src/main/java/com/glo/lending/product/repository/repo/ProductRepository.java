package com.glo.lending.product.repository.repo;

import com.glo.lending.product.repository.entities.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Reactive repository for {@link Product} entities.
 */
@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, UUID> {

    /**
     * Finds all products matching the given status.
     *
     * @param status the product status to filter by
     * @return a {@link Flux} emitting matching products
     */
    Flux<Product> findByStatus(String status);
}

