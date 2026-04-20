package com.glo.lending.product.model.dto;

import com.glo.lending.product.model.enums.CalculationType;
import com.glo.lending.product.model.enums.FeeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeRequest {
        @NotNull(message = "Fee type is required")
        FeeType feeType;

        @NotNull(message = "Calculation type is required")
        CalculationType calculationType;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.00", inclusive = false, message = "Fee amount must be greater than zero")
        BigDecimal amount;

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description;

        Integer triggerDaysAfterDue;

        Boolean applyAtOrigination;
}

