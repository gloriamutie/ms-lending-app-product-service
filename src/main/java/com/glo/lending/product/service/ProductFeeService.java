package com.glo.lending.product.service;

import com.glo.lending.product.model.dto.FeeRequest;
import com.glo.lending.product.model.dto.FeeResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductFeeService {
     Mono<FeeResponse> addFee( UUID productId,  FeeRequest request);
     Mono<Void> removeFee( UUID productId,  UUID feeId);
}
