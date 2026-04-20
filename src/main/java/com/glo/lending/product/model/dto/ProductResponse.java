package com.glo.lending.product.model.dto;

import com.glo.lending.product.model.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO representing a complete loan product with its configurations.
 *
 * @param id          unique product identifier
 * @param name        product name
 * @param description product description
 * @param minAmount   minimum loan amount
 * @param maxAmount   maximum loan amount
 * @param currency    ISO 4217 currency code
 * @param status      current product status
 * @param tenures     associated tenure options
 * @param fees        associated fee configurations
 * @param createdAt   creation timestamp
 * @param updatedAt   last update timestamp
 */
public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String currency,
        ProductStatus status,
        List<TenureResponse> tenures,
        List<FeeResponse> fees,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

