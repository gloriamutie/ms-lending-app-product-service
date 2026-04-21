# Product Service (ms-lending-app-product-service)

Manages loan product definitions, tenure options, and fee configurations for the lending platform.

## Tech Stack

| Component        | Technology                              |
|------------------|-----------------------------------------|
| Framework        | Spring Boot 3.4.4 / Spring WebFlux      |
| Language         | Java 21                                 |
| Database         | PostgreSQL (R2DBC — reactive)           |
| Migrations       | Flyway (runs over JDBC at startup)      |
| Caching          | Spring Cache (`CaffeineCacheManager`) |
| Security         | API Key (`X-API-KEY` header)            |
| Testing          | JUnit 5 + Mockito + StepVerifier        |
| Code Coverage    | JaCoCo                                  |

## Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 15+
- Create database: `CREATE DATABASE lending_product_db;`

## Getting Started

```bash
# Clone and navigate
cd ms-lending-app-product-service

# Build
mvn clean compile

# Run
mvn spring-boot:run

# Run tests
mvn clean test

# Generate coverage report
mvn clean test jacoco:report
# Report at: target/site/jacoco/index.html
```

The service starts on **port 8080** and Flyway auto-creates all tables on first startup.

## Configuration

Key properties in `src/main/resources/application.properties`:

| Property                  | Default                                              |
|---------------------------|------------------------------------------------------|
| `server.port`             | `8080`                                               |
| `spring.r2dbc.url`        | `r2dbc:postgresql://localhost:5432/lending_product_db`|
| `app.security.api-key`    | `product-service-api-key-2024`                       |
| `spring.flyway.enabled`   | `true`                                               |
| `spring.cache.type`       | `simple`                                             |

## Database Schema

Flyway migration `V1__init_schema.sql` creates:

- **products** — loan product definitions (name, amount range, currency, status)
- **product_tenures** — tenure options per product (value, type DAYS/MONTHS, fixed/flexible)
- **product_fees** — fee configurations per product (SERVICE_FEE, DAILY_FEE, LATE_FEE with FIXED/PERCENTAGE calculation)

`V2__seed_data.sql` inserts demo products: Quick Cash Loan, Salary Advance Loan, Legacy Micro Loan.

## API Endpoints

All endpoints require header: `X-API-KEY: product-service-api-key-2024`

### Products

| Method   | Endpoint                                | Description                         |
|----------|-----------------------------------------|-------------------------------------|
| `POST`   | `/api/v1/products`                      | Create product with tenures + fees  |
| `GET`    | `/api/v1/products`                      | List all (optional `?status=ACTIVE`)|
| `GET`    | `/api/v1/products/{productId}`          | Get product by ID (cached)          |
| `PUT`    | `/api/v1/products/{productId}`          | Update product details              |
| `DELETE` | `/api/v1/products/{productId}`          | Delete product + cascade            |

### Fees

| Method   | Endpoint                                       | Description        |
|----------|-------------------------------------------------|--------------------|
| `POST`   | `/api/v1/products/{productId}/fees`             | Add fee to product |
| `DELETE` | `/api/v1/products/{productId}/fees/{feeId}`     | Remove fee         |

### Tenures

| Method   | Endpoint                                          | Description           |
|----------|---------------------------------------------------|-----------------------|
| `POST`   | `/api/v1/products/{productId}/tenures`            | Add tenure to product |
| `DELETE` | `/api/v1/products/{productId}/tenures/{tenureId}` | Remove tenure         |

## Example Request — Create Product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: product-service-api-key-2024" \
  -d '{
    "name": "Quick Cash Loan",
    "description": "Short-term personal loan",
    "minAmount": 500.00,
    "maxAmount": 50000.00,
    "currency": "KES",
    "status": "ACTIVE",
    "tenures": [
      { "tenureValue": 30, "tenureType": "DAYS", "isFixed": true }
    ],
    "fees": [
      { "feeType": "SERVICE_FEE", "calculationType": "PERCENTAGE", "amount": 5.0, "description": "Origination fee", "applyAtOrigination": true }
    ]
  }'
```

## Project Structure

```
src/main/java/com/glo/lending/product/
├── ProductServiceApplication.java
├── components/
│   └── ProductCache.java              # Spring CacheManager wrapper
├── config/
│   ├── CacheConfig.java               # @EnableCaching + ConcurrentMapCacheManager
│   ├── R2dbcConfig.java               # @EnableR2dbcAuditing
│   └── SecurityConfig.java            # API Key auth filter
├── controller/
│   └── ProductController.java         # REST endpoints
├── dblayer/
│   ├── entities/                      # R2DBC entities (Product, ProductFee, ProductTenure)
│   └── repo/                          # ReactiveCrudRepository interfaces
├── exception/
│   ├── GlobalExceptionHandler.java    # @RestControllerAdvice
│   └── ProductNotFoundException.java
├── model/
│   ├── dto/                           # Request/Response records
│   └── enums/                         # ProductStatus, FeeType, CalculationType, TenureType
├── service/
│   ├── ProductService.java            # Interface
│   ├── ProductFeeService.java         # Interface
│   ├── ProductTenureService.java      # Interface
│   └── serviceImpl/                   # Implementations
└── utils/
    └── ProductMapper.java             # Entity ↔ DTO mapping
```

