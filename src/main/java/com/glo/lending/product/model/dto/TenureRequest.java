package com.glo.lending.product.model.dto;

import com.glo.lending.product.model.enums.TenureType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenureRequest {
        @NotNull(message = "Tenure value is required")
        @Min(value = 1, message = "Tenure value must be at least 1")
        Integer tenureValue;

        @NotNull(message = "Tenure type is required")
        TenureType tenureType;

        @NotNull(message = "Fixed tenure flag is required")
        Boolean isFixed;
}

