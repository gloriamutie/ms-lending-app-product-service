package com.glo.lending.product.model.dto;


import java.time.LocalDateTime;

public record ApiResponse<T>(
        int status,
        String message,
        T data,
        LocalDateTime timestamp
) {
}