package com.glo.lending.product.model.dto;

import com.glo.lending.product.model.enums.CalculationType;
import com.glo.lending.product.model.enums.FeeType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for a fee configuration.
 *
 * @param id                   unique fee identifier
 * @param feeType              category of fee
 * @param calculationType      FIXED or PERCENTAGE
 * @param amount               fee amount or percentage value
 * @param description          optional description
 * @param triggerDaysAfterDue  days after due before fee triggers
 * @param applyAtOrigination   whether fee applies at origination
 */
public record FeeResponse(
        UUID id,
        FeeType feeType,
        CalculationType calculationType,
        BigDecimal amount,
        String description,
        Integer triggerDaysAfterDue,
        Boolean applyAtOrigination
) {
}

