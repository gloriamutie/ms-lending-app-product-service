package com.glo.lending.product.service.serviceImpl;

import com.glo.lending.product.dblayer.entities.Product;
import com.glo.lending.product.dblayer.entities.ProductFee;
import com.glo.lending.product.dblayer.entities.ProductTenure;
import com.glo.lending.product.dblayer.repo.ProductFeeRepository;
import com.glo.lending.product.dblayer.repo.ProductRepository;
import com.glo.lending.product.dblayer.repo.ProductTenureRepository;
import com.glo.lending.product.exception.ProductNotFoundException;
import com.glo.lending.product.model.dto.*;
import com.glo.lending.product.model.enums.ProductStatus;
import com.glo.lending.product.components.ProductCache;
import com.glo.lending.product.service.ProductService;
import com.glo.lending.product.utils.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductFeeRepository productFeeRepository;
    private final ProductTenureRepository productTenureRepository;
    private final ProductCache cacheService;


    @Override
    public Mono<ProductResponse> createProduct(CreateProductRequest request) {
        log.info("Creating product: name={}, currency={}", request.getName(), request.getCurrency());

        if (request.getMaxAmount().compareTo(request.getMinAmount()) < 0) {
            return Mono.error(new IllegalArgumentException("Maximum amount must be greater than or equal to minimum amount"));
        }

        final Product product = ProductMapper.toEntity(request);

        return productRepository.save(product)
                .flatMap(savedProduct -> {
                    final UUID productId = savedProduct.getId();
                    log.info("Product saved with id: {}", productId);

                    final Mono<List<ProductTenure>> savedTenures = saveTenures(request.getTenures(), productId);
                    final Mono<List<ProductFee>> savedFees = saveFees(request.getFee(), productId);

                    return Mono.zip(savedTenures, savedFees)
                            .map(tuple -> ProductMapper.toResponse(savedProduct, tuple.getT2(), tuple.getT1()));
                })
                .doOnSuccess(resp -> log.info("Product created successfully: id={}", resp.id()))
                .doOnError(error -> log.error("Failed to create product: {}", error.getMessage()));
    }


     // Retrieves a product by ID with its tenures and fees, using cache.
    @Override
    public Mono<ProductResponse> getProductById(final UUID productId) {
        log.debug("Fetching product: {}", productId);

        return cacheService.getProductById(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))
                .flatMap(product -> {
                    final Mono<List<ProductFee>> fees = cacheService.getFeesByProductId(productId).collectList();
                    final Mono<List<ProductTenure>> tenures = cacheService.getTenuresByProductId(productId).collectList();

                    return Mono.zip(fees, tenures)
                            .map(tuple -> ProductMapper.toResponse(product, tuple.getT1(), tuple.getT2()));
                });
    }


     // Retrieves all products, optionally filtered by status.
    @Override
    public Flux<ProductResponse> getAllProducts(final ProductStatus status) {
        log.debug("Fetching all products, status filter: {}", status);

        final Flux<Product> products = (status != null)
                ? productRepository.findByStatus(status.name())
                : productRepository.findAll();

        return products.flatMap(product -> {
            final Mono<List<ProductFee>> fees = productFeeRepository.findByProductId(product.getId()).collectList();
            final Mono<List<ProductTenure>> tenures = productTenureRepository.findByProductId(product.getId()).collectList();

            return Mono.zip(fees, tenures)
                    .map(tuple -> ProductMapper.toResponse(product, tuple.getT1(), tuple.getT2()));
        });
    }


    //Updates an existing product's details. Only non-null fields in the request are applied. After updating, the cache is evicted to ensure consistency.
    @Override
    public Mono<ProductResponse> updateProduct(final UUID productId, final UpdateProductRequest request) {
        log.info("Updating product: {}", productId);

        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))
                .flatMap(existing -> {
                    if (request.getName() != null) existing.setName(request.getName());
                    if (request.getDescription() != null) existing.setDescription(request.getDescription());
                    if (request.getMinAmount() != null) existing.setMinAmount(request.getMinAmount());
                    if (request.getMaxAmount() != null) existing.setMaxAmount(request.getMaxAmount());
                    if (request.getCurrency() != null) existing.setCurrency(request.getCurrency());
                    if (request.getStatus() != null) existing.setStatus(request.getStatus());
                    existing.setUpdatedAt(LocalDateTime.now());

                    return productRepository.save(existing);
                })
                .flatMap(saved -> {
                    cacheService.evictProduct(productId);
                    return getProductById(productId);
                })
                .doOnSuccess(resp -> log.info("Product updated: id={}", productId));
    }


    //Deletes a product and all its associated tenures and fees.
    @Override
    public Mono<Void> deleteProduct(final UUID productId) {
        log.info("Deleting product: {}", productId);

        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))
                .flatMap(product ->
                        productFeeRepository.findByProductId(productId).collectList()
                                .flatMap(fees -> productFeeRepository.deleteAll(fees))
                                .then(productTenureRepository.findByProductId(productId).collectList())
                                .flatMap(tenures -> productTenureRepository.deleteAll(tenures))
                                .then(productRepository.delete(product))
                )
                .doOnSuccess(v -> {
                    cacheService.evictProduct(productId);
                    log.info("Product deleted: {}", productId);
                });
    }


    private Mono<List<ProductTenure>> saveTenures(final List<TenureRequest> tenures, final UUID productId) {
        if (tenures == null || tenures.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }
        return Flux.fromIterable(tenures)
                .map(req -> ProductMapper.toEntity(req, productId))
                .flatMap(productTenureRepository::save)
                .collectList();
    }



    private Mono<List<ProductFee>> saveFees(final List<FeeRequest> fees, final UUID productId) {
        if (fees == null || fees.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }
        return Flux.fromIterable(fees)
                .map(req -> ProductMapper.toEntity(req, productId))
                .flatMap(productFeeRepository::save)
                .collectList();
    }
}

