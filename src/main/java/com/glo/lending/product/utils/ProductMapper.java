package com.glo.lending.product.utils;

import com.glo.lending.product.model.dto.*;
import com.glo.lending.product.repository.entities.Product;
import com.glo.lending.product.repository.entities.ProductFee;
import com.glo.lending.product.repository.entities.ProductTenure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Maps between Product domain entities and DTOs.
 * Stateless utility — all methods are static.
 */
public final class ProductMapper {

    private ProductMapper() {
    }



    public static Product toEntity( CreateProductRequest request) {

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .minAmount(request.getMinAmount())
                .maxAmount(request.getMaxAmount())
                .currency(request.getCurrency())
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


    public static ProductFee toEntity( FeeRequest request, UUID productId) {
        return ProductFee.builder()
                .productId(productId)
                .feeType(request.getFeeType())
                .calculationType(request.getCalculationType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .triggerDaysAfterDue(request.getTriggerDaysAfterDue())
                .applyAtOrigination(request.getApplyAtOrigination())
                .build();
    }


    public static ProductTenure toEntity(TenureRequest request,  UUID productId) {
        return ProductTenure.builder()
                .productId(productId)
                .tenureValue(request.getTenureValue())
                .tenureType(request.getTenureType())
                .isFixed(request.getIsFixed())
                .build();
    }

    public static ProductResponse toResponse( Product product,  List<ProductFee> fees, List<ProductTenure> tenures) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getMinAmount(),
                product.getMaxAmount(),
                product.getCurrency(),
                product.getStatus(),
                tenures.stream().map(ProductMapper::toTenureResponse).toList(),
                fees.stream().map(ProductMapper::toFeeResponse).toList(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public static FeeResponse toFeeResponse(ProductFee fee) {
        return new FeeResponse(
                fee.getId(),
                fee.getFeeType(),
                fee.getCalculationType(),
                fee.getAmount(),
                fee.getDescription(),
                fee.getTriggerDaysAfterDue(),
                fee.getApplyAtOrigination()
        );
    }


    public static TenureResponse toTenureResponse(ProductTenure tenure) {
        return new TenureResponse(
                tenure.getId(),
                tenure.getTenureValue(),
                tenure.getTenureType(),
                tenure.getIsFixed()
        );
    }
}

