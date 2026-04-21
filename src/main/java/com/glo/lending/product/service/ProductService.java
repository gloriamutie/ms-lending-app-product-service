package com.glo.lending.product.service;

import com.glo.lending.product.model.dto.CreateProductRequest;
import com.glo.lending.product.model.dto.ProductResponse;
import com.glo.lending.product.model.dto.UpdateProductRequest;
import com.glo.lending.product.model.enums.ProductStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductService {
    public Mono<ProductResponse> createProduct(CreateProductRequest request);
    public Mono<ProductResponse> getProductById(final UUID productId);
    public Flux<ProductResponse> getAllProducts(final ProductStatus status);
    public Mono<ProductResponse> updateProduct(final UUID productId, final UpdateProductRequest request);
    public Mono<Void> deleteProduct(final UUID productId);


}
