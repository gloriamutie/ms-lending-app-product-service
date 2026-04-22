package com.glo.lending.product.dblayer.repo;

import com.glo.lending.product.dblayer.entities.Product;
import com.glo.lending.product.model.enums.FeeType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, UUID> {

    Flux<Product> findByStatus(String status);

}

