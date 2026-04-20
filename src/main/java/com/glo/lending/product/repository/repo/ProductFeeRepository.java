package com.glo.lending.product.repository.repo;

import com.glo.lending.product.repository.entities.ProductFee;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;


@Repository
public interface ProductFeeRepository extends ReactiveCrudRepository<ProductFee, UUID> {

    Flux<ProductFee> findByProductId(UUID productId);
}

