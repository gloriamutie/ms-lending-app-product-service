package com.glo.lending.product.exception;

import com.glo.lending.product.model.dto.ApiResponse;
import com.glo.lending.product.service.serviceImpl.ProductFeeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for the Product Service REST API.
 * Translates domain and validation exceptions into structured JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleProductNotFound(final ProductNotFoundException ex) {
        log.warn("Product not found: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(HttpStatus.NOT_FOUND, ex.getMessage())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidation(final WebExchangeBindException ex) {
        final Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid", (a, b) -> a));
        log.warn("Validation failed: {}", fieldErrors);
        final Map<String, Object> body = buildError(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("errors", fieldErrors);
        return Mono.just(ResponseEntity.badRequest().body(body));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgument(final IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return Mono.just(ResponseEntity.badRequest().body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGeneric(final Exception ex) {
        log.error("Unexpected error", ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")));
    }

    private Map<String, Object> buildError(final HttpStatus status, final String message) {
        final Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        return error;
    }

    @ExceptionHandler(ProductFeeServiceImpl.DuplicateFeeException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDuplicateFee(ProductFeeServiceImpl.DuplicateFeeException ex) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(
                                409,
                                ex.getMessage(),
                                null,
                                LocalDateTime.now()
                        ))
        );
    }
}

