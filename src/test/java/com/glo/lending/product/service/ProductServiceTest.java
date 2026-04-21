package com.glo.lending.product.service;

import com.glo.lending.product.dblayer.entities.ProductFee;
import com.glo.lending.product.dblayer.entities.ProductTenure;
import com.glo.lending.product.dblayer.repo.ProductRepository;
import com.glo.lending.product.exception.ProductNotFoundException;
import com.glo.lending.product.model.dto.*;
import com.glo.lending.product.model.enums.CalculationType;
import com.glo.lending.product.model.enums.FeeType;
import com.glo.lending.product.model.enums.ProductStatus;
import com.glo.lending.product.model.enums.TenureType;
import com.glo.lending.product.dblayer.entities.Product;
import com.glo.lending.product.dblayer.repo.ProductFeeRepository;
import com.glo.lending.product.dblayer.repo.ProductTenureRepository;
import com.glo.lending.product.components.ProductCache;
import com.glo.lending.product.service.serviceImpl.ProductFeeServiceImpl;
import com.glo.lending.product.service.serviceImpl.ProductServiceImpl;
import com.glo.lending.product.service.serviceImpl.ProductTenureImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductFeeRepository productFeeRepository;
    @Mock
    private ProductTenureRepository productTenureRepository;
    @Mock
    private ProductCache cacheService;

    @InjectMocks
    private ProductServiceImpl productService;
    @InjectMocks
    private ProductFeeServiceImpl productFeeService;
    @InjectMocks
    private ProductTenureImpl productTenureService;

    private Product product;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);
        product.setName("Quick Cash Loan");
        product.setDescription("Short-term loan");
        product.setMinAmount(BigDecimal.valueOf(500));
        product.setMaxAmount(BigDecimal.valueOf(50000));
        product.setCurrency("KES");
        product.setStatus(ProductStatus.ACTIVE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("should create product with tenures and fees successfully")
        void createProduct_ValidRequest_ReturnsProductResponse() {
            // Given
            final CreateProductRequest request = new CreateProductRequest(
                    "Test Loan", "Description", BigDecimal.valueOf(100), BigDecimal.valueOf(10000),
                    "KES", ProductStatus.ACTIVE,
                    List.of(new TenureRequest(30, TenureType.DAYS, true)),
                    List.of(new FeeRequest(FeeType.SERVICE_FEE, CalculationType.FIXED, BigDecimal.TEN, "Fee", null, true))
            );

            ProductTenure tenure = ProductTenure.builder()
                    .id(UUID.randomUUID())
                    .productId(productId)
                    .tenureValue(30)
                    .tenureType(TenureType.DAYS)
                    .isFixed(true)
                    .build();

            ProductFee fee = ProductFee.builder()
                    .id(UUID.randomUUID())
                    .productId(productId)
                    .feeType(FeeType.SERVICE_FEE)
                    .calculationType(CalculationType.FIXED)
                    .amount(BigDecimal.TEN)
                    .build();

            when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
            when(productTenureRepository.save(any(ProductTenure.class))).thenReturn(Mono.just(tenure));
            when(productFeeRepository.save(any(ProductFee.class))).thenReturn(Mono.just(fee));

            // When & Then
            StepVerifier.create(productService.createProduct(request))
                    .assertNext(response -> {
                        assertEquals("Quick Cash Loan", response.name());
                        assertNotNull(response.tenures());
                        assertNotNull(response.fees());
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should fail when maxAmount < minAmount")
        void createProduct_InvalidAmounts_ReturnsError() {
            // Given
            final CreateProductRequest request = new CreateProductRequest(
                    "Test", "Desc", BigDecimal.valueOf(10000), BigDecimal.valueOf(100),
                    "KES", ProductStatus.ACTIVE, null, null
            );

            // When & Then
            StepVerifier.create(productService.createProduct(request))
                    .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                            e.getMessage().contains("Maximum amount must be greater"))
                    .verify();
        }

        @Test
        @DisplayName("should create product with empty tenures and fees")
        void createProduct_NoTenuresOrFees_ReturnsProductWithEmptyLists() {
            // Given
            final CreateProductRequest request = new CreateProductRequest(
                    "Basic Loan", "Desc", BigDecimal.ONE, BigDecimal.TEN,
                    "KES", ProductStatus.ACTIVE, null, null
            );
            when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));

            // When & Then
            StepVerifier.create(productService.createProduct(request))
                    .assertNext(response -> {
                        assertTrue(response.tenures().isEmpty());
                        assertTrue(response.fees().isEmpty());
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @Test
        @DisplayName("should return product from cache")
        void getProductById_Exists_ReturnsProduct() {
            // Given
            when(cacheService.getProductById(productId)).thenReturn(Mono.just(product));
            when(cacheService.getFeesByProductId(productId)).thenReturn(Flux.empty());
            when(cacheService.getTenuresByProductId(productId)).thenReturn(Flux.empty());

            // When & Then
            StepVerifier.create(productService.getProductById(productId))
                    .assertNext(response -> assertEquals(productId, response.id()))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should throw ProductNotFoundException when not found")
        void getProductById_NotExists_ThrowsException() {
            // Given
            when(cacheService.getProductById(productId)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(productService.getProductById(productId))
                    .expectError(ProductNotFoundException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("getAllProducts")
    class GetAllProducts {

        @Test
        @DisplayName("should return all products when no status filter")
        void getAllProducts_NoFilter_ReturnsAll() {
            // Given
            when(productRepository.findAll()).thenReturn(Flux.just(product));
            when(productFeeRepository.findByProductId(productId)).thenReturn(Flux.empty());
            when(productTenureRepository.findByProductId(productId)).thenReturn(Flux.empty());

            // When & Then
            StepVerifier.create(productService.getAllProducts(null))
                    .assertNext(r -> assertEquals("Quick Cash Loan", r.name()))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should filter by status")
        void getAllProducts_WithFilter_ReturnsFiltered() {
            // Given
            when(productRepository.findByStatus("ACTIVE")).thenReturn(Flux.just(product));
            when(productFeeRepository.findByProductId(productId)).thenReturn(Flux.empty());
            when(productTenureRepository.findByProductId(productId)).thenReturn(Flux.empty());

            // When & Then
            StepVerifier.create(productService.getAllProducts(ProductStatus.ACTIVE))
                    .expectNextCount(1)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("updateProduct")
    class UpdateProduct {

        @Test
        @DisplayName("should update only non-null fields")
        void updateProduct_PartialUpdate_UpdatesOnlyProvided() {
            // Given
             UpdateProductRequest request = new UpdateProductRequest();
             request.setName("Updated Name");
            when(productRepository.findById(productId)).thenReturn(Mono.just(product));
            when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
            when(cacheService.getProductById(productId)).thenReturn(Mono.just(product));
            when(cacheService.getFeesByProductId(productId)).thenReturn(Flux.empty());
            when(cacheService.getTenuresByProductId(productId)).thenReturn(Flux.empty());

            // When & Then
            StepVerifier.create(productService.updateProduct(productId, request))
                    .assertNext(r -> assertNotNull(r.id()))
                    .verifyComplete();

            verify(cacheService).evictProduct(productId);
        }

        @Test
        @DisplayName("should throw when product not found")
        void updateProduct_NotFound_ThrowsException() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Mono.empty());

            UpdateProductRequest updateProductRequest = new UpdateProductRequest();
             updateProductRequest.setName(null);
             updateProductRequest.setDescription(null);
             updateProductRequest.setMinAmount(null);
             updateProductRequest.setMaxAmount(null);
             updateProductRequest.setCurrency(null);
             updateProductRequest.setStatus(null);

            // When & Then
            StepVerifier.create(productService.updateProduct(productId, updateProductRequest))
                    .expectError(ProductNotFoundException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("deleteProduct")
    class DeleteProduct {

        @Test
        @DisplayName("should delete product with fees and tenures")
        void deleteProduct_Exists_DeletesAll() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Mono.just(product));
            when(productFeeRepository.findByProductId(productId)).thenReturn(Flux.empty());
            when(productFeeRepository.deleteAll(any(Iterable.class))).thenReturn(Mono.empty());
            when(productTenureRepository.findByProductId(productId)).thenReturn(Flux.empty());
            when(productTenureRepository.deleteAll(any(Iterable.class))).thenReturn(Mono.empty());
            when(productRepository.delete(product)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(productService.deleteProduct(productId))
                    .verifyComplete();

            verify(cacheService).evictProduct(productId);
        }

        @Test
        @DisplayName("should throw when product not found")
        void deleteProduct_NotFound_ThrowsException() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(productService.deleteProduct(productId))
                    .expectError(ProductNotFoundException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Fee Management")
    class FeeManagement {

        @Test
        @DisplayName("should add fee to existing product")
        void addFee_ValidRequest_ReturnsFeeResponse() {
            // Given
            final FeeRequest request = new FeeRequest(FeeType.LATE_FEE, CalculationType.FIXED, BigDecimal.valueOf(500), "Late fee", 3, false);
            ProductFee fee = ProductFee.builder()
                    .id(UUID.randomUUID())
                    .productId(productId)
                    .feeType(FeeType.LATE_FEE)
                    .calculationType(CalculationType.FIXED)
                    .amount(BigDecimal.valueOf(500))
                    .build();


            when(productRepository.findById(productId)).thenReturn(Mono.just(product));
            when(productFeeRepository.save(any(ProductFee.class))).thenReturn(Mono.just(fee));

            // When & Then
            StepVerifier.create(productFeeService.addFee(productId, request))
                    .assertNext(r -> assertEquals(FeeType.LATE_FEE, r.feeType()))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should throw when adding fee to non-existent product")
        void addFee_ProductNotFound_ThrowsException() {
            // Given
            final FeeRequest request = new FeeRequest(FeeType.SERVICE_FEE, CalculationType.FIXED, BigDecimal.ONE, "test", null, null);
            when(productRepository.findById(productId)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(productFeeService.addFee(productId, request))
                    .expectError(ProductNotFoundException.class)
                    .verify();
        }
    }
}

