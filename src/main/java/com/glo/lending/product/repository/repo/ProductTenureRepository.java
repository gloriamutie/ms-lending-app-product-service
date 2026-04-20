package com.glo.lending.product.repository.repo;

import com.glo.lending.product.repository.entities.ProductTenure;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;


@Repository
public interface ProductTenureRepository extends ReactiveCrudRepository<ProductTenure, UUID> {
    Flux<ProductTenure> findByProductId(UUID productId);
}

