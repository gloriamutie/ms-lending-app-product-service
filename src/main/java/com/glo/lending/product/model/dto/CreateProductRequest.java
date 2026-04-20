package com.glo.lending.product.model.dto;

import com.glo.lending.product.model.enums.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest{
        @NotBlank(message = "Product name is required")
        @Size(max = 100, message = "Product name must not exceed 100 characters")
        String name;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description;

        @NotNull(message = "Minimum amount is required")
        @DecimalMin(value = "0.01", message = "Minimum amount must be greater than zero")
        BigDecimal minAmount;

        @NotNull(message = "Maximum amount is required")
        @DecimalMin(value = "0.01", message = "Maximum amount must be greater than zero")
        BigDecimal maxAmount;

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
        String currency;

        @NotNull(message = "Product status is required")
        ProductStatus status;

        @Valid
        List<TenureRequest> tenures;

        @Valid
        List<FeeRequest> fee;
}

