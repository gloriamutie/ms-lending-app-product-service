package com.glo.lending.product.repository.repo;

import com.glo.lending.product.repository.entities.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, UUID> {

    Flux<Product> findByStatus(String status);
}

