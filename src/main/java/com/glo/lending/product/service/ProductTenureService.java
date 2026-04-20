package com.glo.lending.product.service;

import com.glo.lending.product.model.dto.TenureRequest;
import com.glo.lending.product.model.dto.TenureResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductTenureService {
    Mono<TenureResponse> addTenure(final UUID productId, final TenureRequest request);
     Mono<Void> removeTenure(final UUID productId, final UUID tenureId);

}
