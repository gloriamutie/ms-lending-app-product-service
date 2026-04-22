package com.glo.lending.product.dblayer.repo;

import com.glo.lending.product.dblayer.entities.ProductFee;
import com.glo.lending.product.model.enums.FeeType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Repository
public interface ProductFeeRepository extends ReactiveCrudRepository<ProductFee, UUID> {

    Flux<ProductFee> findByProductId(UUID productId);
    Mono<ProductFee> deleteProductFeeByProductId(UUID productId);
    Mono<Boolean> existsByProductIdAndFeeType(UUID productId, FeeType feeType);

}

