-- =====================================================
-- V1: Product Service Schema
-- Flyway migration — auto-creates tables on startup
-- =====================================================
--   this an extension required for generating UUIDs in PostgreSQL
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Loan Products table
CREATE TABLE IF NOT EXISTS products (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100)   NOT NULL,
    description     VARCHAR(500),
    min_amount      NUMERIC(15,2)  NOT NULL,
    max_amount      NUMERIC(15,2)  NOT NULL,
    currency        VARCHAR(3)     NOT NULL DEFAULT 'KES',
    status          VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_product_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DEPRECATED')),
    CONSTRAINT chk_amount_range CHECK (max_amount >= min_amount)
);

-- Product Tenure Options
CREATE TABLE IF NOT EXISTS product_tenures (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID           NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tenure_value    INT            NOT NULL,
    tenure_type     VARCHAR(10)    NOT NULL,
    is_fixed        BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_tenure_type CHECK (tenure_type IN ('DAYS', 'MONTHS')),
    CONSTRAINT chk_tenure_value CHECK (tenure_value > 0)
);

-- Product Fee Configurations
CREATE TABLE IF NOT EXISTS product_fees (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id              UUID           NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    fee_type                VARCHAR(20)    NOT NULL,
    calculation_type        VARCHAR(20)    NOT NULL,
    amount                  NUMERIC(15,4)  NOT NULL,
    description             VARCHAR(255),
    trigger_days_after_due  INT,
    apply_at_origination    BOOLEAN        DEFAULT FALSE,

    CONSTRAINT chk_fee_type CHECK (fee_type IN ('SERVICE_FEE', 'DAILY_FEE', 'LATE_FEE')),
    CONSTRAINT chk_calc_type CHECK (calculation_type IN ('FIXED', 'PERCENTAGE')),
    CONSTRAINT chk_fee_amount CHECK (amount > 0)
);

CREATE INDEX idx_product_tenures_product ON product_tenures(product_id);
CREATE INDEX idx_product_fees_product ON product_fees(product_id);
CREATE INDEX idx_products_status ON products(status);

