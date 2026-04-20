package com.glo.lending.product.controller;

import com.glo.lending.product.model.dto.*;
import com.glo.lending.product.model.enums.ProductStatus;
import com.glo.lending.product.service.ProductFeeService;
import com.glo.lending.product.service.ProductService;
import com.glo.lending.product.service.ProductTenureService;
import com.glo.lending.product.service.serviceImpl.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final ProductFeeService productFeeService;
    private final ProductTenureService productTenureService;


    @PostMapping("/create")
    public Mono<ResponseEntity<ProductResponse>> createProduct(@Valid @RequestBody  CreateProductRequest request) {
        log.info("Create Product={}", request.getName());
        return productService.createProduct(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{productId}")
    public Mono<ResponseEntity<ProductResponse>> getProduct(@PathVariable  UUID productId) {
        log.info("GET products by id {}", productId);
        return productService.getProductById(productId)
                .map(product -> ResponseEntity.ok().body(product));
    }


    @GetMapping("/getAll")
    public Mono<ResponseEntity<Flux<ProductResponse>>> getAllProducts(@RequestParam(required = false)  ProductStatus status) {
        log.info("GET all products by status: {}", status);
        return Mono.just(ResponseEntity.ok(productService.getAllProducts(status)));
    }


    @PutMapping("/update/{productId}")
    public Mono<ResponseEntity<ProductResponse>> updateProduct(@PathVariable  UUID productId,
                                                               @Valid @RequestBody  UpdateProductRequest request) {
        log.info("Update product by productId: {}", productId);
        return productService.updateProduct(productId, request)
                .map(product -> ResponseEntity.ok().body(product));
    }

    @DeleteMapping("/{productId}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable  UUID productId) {
        log.info("Delete product by id: {}", productId);
        return productService.deleteProduct(productId).then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    // ======================== Fee Management ========================


    @PostMapping("/{productId}/fees")
    public Mono<ResponseEntity<FeeResponse>> addFee(@PathVariable  UUID productId,
                                                    @Valid @RequestBody  FeeRequest request) {
        log.info("Add fee to a product {} / fee type{}", productId, request.getFeeType());
        return productFeeService.addFee(productId, request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }


    @DeleteMapping("/{productId}/fees/{feeId}")
    public Mono<ResponseEntity<Void>> removeFee(@PathVariable  UUID productId, @PathVariable  UUID feeId) {
        log.info("Delete a fee configuration from a product {}/fees/{}", productId, feeId);
        return productFeeService.removeFee(productId, feeId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    // ======================== Tenure Management ========================


    @PostMapping("/{productId}/tenures")
    public Mono<ResponseEntity<TenureResponse>> addTenure(@PathVariable  UUID productId,
                                                          @Valid @RequestBody  TenureRequest request) {
        log.info("Adds tenure option to a product {} /tenures — value={} {}", productId, request.getTenureValue(), request.getTenureType());
        return productTenureService.addTenure(productId, request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @DeleteMapping("/{productId}/tenures/{tenureId}")
    public Mono<ResponseEntity<Void>> removeTenure(@PathVariable  UUID productId, @PathVariable  UUID tenureId) {
        log.info("Remove tenure from a product {}/tenures/{}", productId, tenureId);
        return productTenureService.removeTenure(productId, tenureId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}

