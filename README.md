# Product Service (ms-lending-app-product-service)

Manages loan product definitions, tenure options, and fee configurations for the lending platform.

## Tech Stack

| Component        | Technology                              |
|------------------|-----------------------------------------|
| Framework        | Spring Boot 3.4.8 / Spring WebFlux      |
| Language         | Java 21                                 |
| Database         | PostgreSQL (R2DBC — reactive)           |
| Migrations       | Flyway (runs over JDBC at startup)      |
| Caching          | CaffeineCacheManager                    |
| Security         | Spring Security + API Key (`X-API-KEY`) |
| Messaging        | Spring Kafka                            |
| Testing          | JUnit 5 + Mockito + StepVerifier        |
| Code Coverage    | JaCoCo                                  |

## Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 15+
- Apache Kafka (for event publishing)
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

The service starts on **port 8082** and Flyway auto-creates all tables on first startup.

## Configuration

Key properties in `src/main/resources/application.properties`:

| Property                           | Default                                               |
|------------------------------------|-------------------------------------------------------|
| `server.port`                      | `8082`                                                |
| `spring.r2dbc.url`                 | `r2dbc:postgresql://localhost:5432/lending_product_db` |
| `spring.flyway.url`                | `jdbc:postgresql://localhost:5432/lending_product_db`  |
| `spring.flyway.enabled`            | `true`                                                |
| `spring.flyway.baseline-on-migrate`| `true`                                                |
| `app.security.api-key`             | `product-service-api-key-2024`                        |
| `spring.cache.type`                | `Caffeine`                                              |
| `spring.kafka.bootstrap-servers`   | `localhost:9092`                                      |

### Flyway & Schema Setup

Flyway runs over **JDBC** at startup to execute migrations, while the application uses **R2DBC** for all runtime database access. Both connection URLs must point to the same database.

> **Troubleshooting — Flyway not creating tables:**
> Flyway requires its own JDBC connection properties (`spring.flyway.url`, `spring.flyway.user`, `spring.flyway.password`) to be configured separately from the R2DBC URL. If tables are not being created, verify:
> 1. The `spring.flyway.url` uses a `jdbc:postgresql://` URL (not `r2dbc:`).
> 2. The `spring.flyway.user` and `spring.flyway.password` are set.
> 3. The target database (`lending_product_db`) already exists — Flyway does **not** create the database itself.
> 4. `spring.flyway.enabled=true` and `spring.flyway.baseline-on-migrate=true` are set.
> 5. The `spring-boot-starter-jdbc`, `postgresql` (JDBC driver), and `flyway-database-postgresql` dependencies are present in `pom.xml`.

## Database Schema

Flyway migration `V1__init_schema.sql` creates:

- **products** — loan product definitions (name, amount range, currency, status)
- **product_tenures** — tenure options per product (value, type DAYS/MONTHS, fixed/flexible)
- **product_fees** — fee configurations per product (SERVICE_FEE, DAILY_FEE, LATE_FEE with FIXED/PERCENTAGE calculation)

`V2__seed_data.sql` inserts demo products: Quick Cash Loan, Salary Advance Loan.

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
curl -X POST http://localhost:8082/api/v1/products \
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
│   ├── CacheConfig.java               # @EnableCaching + Caffeine CacheManager
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
│   ├── enums/                         # ProductStatus, FeeType, CalculationType, TenureType
│   └── pojo/                          # Plain objects for internal mapping
├── service/
│   ├── ProductService.java            # Interface
│   ├── ProductFeeService.java         # Interface
│   ├── ProductTenureService.java      # Interface
│   └── serviceImpl/                   # Implementations
└── utils/
    └── ProductMapper.java             # Entity ↔ DTO mapping
```
