package com.glo.lending.product.service.serviceImpl;

import com.glo.lending.product.components.ProductCache;
import com.glo.lending.product.exception.ProductNotFoundException;
import com.glo.lending.product.model.dto.TenureRequest;
import com.glo.lending.product.model.dto.TenureResponse;
import com.glo.lending.product.repository.entities.ProductTenure;
import com.glo.lending.product.repository.repo.ProductFeeRepository;
import com.glo.lending.product.repository.repo.ProductRepository;
import com.glo.lending.product.repository.repo.ProductTenureRepository;
import com.glo.lending.product.service.ProductTenureService;
import com.glo.lending.product.utils.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductTenureImpl implements ProductTenureService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductFeeRepository productFeeRepository;
    private final ProductTenureRepository productTenureRepository;
    private final ProductCache cacheService;

    //Adds a tenure option to an existing product.
    @Override
    public Mono<TenureResponse> addTenure(final UUID productId, final TenureRequest request) {
        log.info("Adding tenure to product {}: value={} {}", productId, request.getTenureValue(), request.getTenureType());

        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))
                .flatMap(p -> productTenureRepository.save(ProductMapper.toEntity(request, productId)))
                .map(ProductMapper::toTenureResponse)
                .doOnSuccess(tenure -> {
                    cacheService.evictProduct(productId);
                    log.info("Tenure added: productId={}, tenureId={}", productId, tenure.id());
                });
    }

    //Removes a tenure option from a product.
    @Override
    public Mono<Void> removeTenure(final UUID productId, final UUID tenureId) {
        log.info("Removing tenure {} from product {}", tenureId, productId);

        return productTenureRepository.deleteById(tenureId)
                .doOnSuccess(v -> cacheService.evictProduct(productId));
    }

}
