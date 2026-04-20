-- =====================================================
-- V2: Product Service Seed Data, smaple data for testing and demonstration
-- Flyway migration — inserts sample products, tenures, and fees
-- =====================================================

-- Product 1: Short-term Personal Loan
INSERT INTO products (id, name, description, min_amount, max_amount, currency, status)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Quick Cash Loan', 'Short term personal loan for emergency needs', 500.00, 50000.00, 'KES', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_tenures (id, product_id, tenure_value, tenure_type, is_fixed)
VALUES ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 30, 'DAYS', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_fees (id, product_id, fee_type, calculation_type, amount, description, apply_at_origination)
VALUES ('21111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'SERVICE_FEE', 'PERCENTAGE', 5.0000, 'Origination fee', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_fees (id, product_id, fee_type, calculation_type, amount, description, trigger_days_after_due)
VALUES ('22111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'LATE_FEE', 'FIXED', 500.0000, 'Late payment penalty', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_fees (id, product_id, fee_type, calculation_type, amount, description)
VALUES ('23111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'DAILY_FEE', 'PERCENTAGE', 0.5000, 'Daily interest accrual on balance')
ON CONFLICT (id) DO NOTHING;

-- Product 2: long-term Installment Loan
INSERT INTO products (id, name, description, min_amount, max_amount, currency, status)
VALUES ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Salary Advance Loan', 'Medium-term installment loan for salaried employees', 10000.00, 500000.00, 'KES', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_tenures (id, product_id, tenure_value, tenure_type, is_fixed)
VALUES ('12111111-1111-1111-1111-111111111111', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 3, 'MONTHS', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_tenures (id, product_id, tenure_value, tenure_type, is_fixed)
VALUES ('13111111-1111-1111-1111-111111111111', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 6, 'MONTHS', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_tenures (id, product_id, tenure_value, tenure_type, is_fixed)
VALUES ('14111111-1111-1111-1111-111111111111', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 12, 'MONTHS', FALSE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_fees (id, product_id, fee_type, calculation_type, amount, description, apply_at_origination)
VALUES ('24111111-1111-1111-1111-111111111111', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'SERVICE_FEE', 'FIXED', 1500.0000, 'Flat processing fee', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_fees (id, product_id, fee_type, calculation_type, amount, description, trigger_days_after_due)
VALUES ('25111111-1111-1111-1111-111111111111', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'LATE_FEE', 'PERCENTAGE', 2.0000, 'Late fee on overdue installment', 5)
ON CONFLICT (id) DO NOTHING;

-- Product 3: Deprecated product
INSERT INTO products (id, name, description, min_amount, max_amount, currency, status)
VALUES ('c3d4e5f6-a7b8-9012-cdef-123456789012', 'Legacy Micro Loan', 'Discontinued micro-loan product', 100.00, 5000.00, 'KES', 'DEPRECATED')
ON CONFLICT (id) DO NOTHING;

