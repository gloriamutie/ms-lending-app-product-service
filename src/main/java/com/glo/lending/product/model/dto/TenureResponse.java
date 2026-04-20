package com.glo.lending.product.model.dto;

import com.glo.lending.product.model.enums.TenureType;

import java.util.UUID;

/**
 * Response DTO for a tenure configuration.
 *
 * @param id          unique tenure identifier
 * @param tenureValue the numeric tenure value
 * @param tenureType  DAYS or MONTHS
 * @param isFixed     whether this is a fixed term
 */
public record TenureResponse(
        UUID id,
        Integer tenureValue,
        TenureType tenureType,
        Boolean isFixed
) {
}

