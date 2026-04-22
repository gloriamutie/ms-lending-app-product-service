package com.glo.lending.product.service.serviceImpl;

import com.glo.lending.product.components.ProductCache;
import com.glo.lending.product.dblayer.repo.ProductFeeRepository;
import com.glo.lending.product.dblayer.repo.ProductRepository;
import com.glo.lending.product.exception.ProductNotFoundException;
import com.glo.lending.product.model.dto.FeeRequest;
import com.glo.lending.product.model.dto.FeeResponse;
import com.glo.lending.product.service.ProductFeeService;
import com.glo.lending.product.utils.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductFeeServiceImpl implements ProductFeeService {
    private static final Logger log = LoggerFactory.getLogger(ProductFeeServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductFeeRepository productFeeRepository;
    private final ProductCache cacheService;

    //Adds a fee to an existing product.
    @Override
    public Mono<FeeResponse> addFee(final UUID productId, FeeRequest request) {
        log.info("Adding fee to product {}: type={}", productId, request.getFeeType());

        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))

                .flatMap(p ->
                        productFeeRepository.existsByProductIdAndFeeType(productId, request.getFeeType())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new DuplicateFeeException(
                                                "Fee type already exists for this product"
                                        ));
                                    }

                                    return productFeeRepository.save(
                                            ProductMapper.toEntity(request, productId)
                                    );
                                })
                )

                .map(ProductMapper::toFeeResponse)

                .flatMap(fee ->
                        Mono.fromRunnable(() -> cacheService.evictProduct(productId))
                                .onErrorResume(e -> {
                                    log.error("Cache eviction failed", e);
                                    return Mono.empty();
                                })
                                .thenReturn(fee)
                );
    }

    //TODO handle reponse headers body is emptu
    //Removes a fee from a product.
    @Override
    public Mono<Void> removeFee(final UUID productId, final UUID feeId) {
        log.info("Removing fee {} from product {}", feeId, productId);

        return productFeeRepository.deleteById(feeId)
                .then(
                        Mono.fromRunnable(() -> cacheService.evictProduct(productId))
                                .onErrorResume(e -> {
                                    log.error("Cache eviction failed", e);
                                    return Mono.empty();
                                })
                )
                .then();
    }

    public class DuplicateFeeException extends RuntimeException {
        public DuplicateFeeException(String message) {
            super(message);
        }
    }



}
