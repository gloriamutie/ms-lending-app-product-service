package com.glo.lending.product.model.dto;

import com.glo.lending.product.model.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
        @Size(max = 100, message = "Product name must not exceed 100 characters")
        String name;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description;

        @DecimalMin(value = "0.01", message = "Minimum amount must be greater than zero")
        BigDecimal minAmount;

        @DecimalMin(value = "0.01", message = "Maximum amount must be greater than zero")
        BigDecimal maxAmount;

        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
        String currency;

        ProductStatus status;
}

