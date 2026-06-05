-- Ensures the current BIGINT-based schema exists when Flyway history exists but tables were removed.
CREATE TABLE IF NOT EXISTS app_users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    role VARCHAR(30) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    otp_code VARCHAR(10),
    otp_expires_at TIMESTAMP,
    password_reset_token VARCHAR(255),
    password_reset_expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    national_id VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    address VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS meters (
    id BIGSERIAL PRIMARY KEY,
    meter_number VARCHAR(80) NOT NULL UNIQUE,
    meter_type VARCHAR(30) NOT NULL,
    installation_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    customer_id BIGINT NOT NULL REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS meter_readings (
    id BIGSERIAL PRIMARY KEY,
    meter_id BIGINT NOT NULL REFERENCES meters(id),
    previous_reading DOUBLE PRECISION NOT NULL,
    current_reading DOUBLE PRECISION NOT NULL,
    consumption DOUBLE PRECISION NOT NULL,
    reading_date DATE NOT NULL,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    recorded_by BIGINT NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_meter_reading_month_year UNIQUE (meter_id, month, year)
);

CREATE TABLE IF NOT EXISTS tariffs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    meter_type VARCHAR(30) NOT NULL,
    tariff_type VARCHAR(30) NOT NULL,
    price_per_unit DOUBLE PRECISION NOT NULL,
    fixed_charge DOUBLE PRECISION NOT NULL,
    vat_percent DOUBLE PRECISION NOT NULL,
    late_penalty_percent DOUBLE PRECISION NOT NULL,
    effective_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    version INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tariff_tiers (
    id BIGSERIAL PRIMARY KEY,
    tariff_id BIGINT NOT NULL REFERENCES tariffs(id) ON DELETE CASCADE,
    min_units DOUBLE PRECISION NOT NULL,
    max_units DOUBLE PRECISION,
    price_per_unit DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS bills (
    id BIGSERIAL PRIMARY KEY,
    bill_reference VARCHAR(40) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    meter_id BIGINT NOT NULL REFERENCES meters(id),
    meter_reading_id BIGINT NOT NULL UNIQUE REFERENCES meter_readings(id),
    tariff_id BIGINT NOT NULL REFERENCES tariffs(id),
    consumption DOUBLE PRECISION NOT NULL,
    unit_charge DOUBLE PRECISION NOT NULL,
    fixed_charge DOUBLE PRECISION NOT NULL,
    vat_amount DOUBLE PRECISION NOT NULL,
    penalty_amount DOUBLE PRECISION NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    amount_paid DOUBLE PRECISION NOT NULL DEFAULT 0,
    outstanding_balance DOUBLE PRECISION NOT NULL,
    billing_month INTEGER NOT NULL,
    billing_year INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    due_date DATE NOT NULL,
    approved_by BIGINT REFERENCES app_users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL REFERENCES bills(id),
    amount_paid DOUBLE PRECISION NOT NULL,
    payment_method VARCHAR(40) NOT NULL,
    payment_date DATE NOT NULL,
    recorded_by BIGINT NOT NULL REFERENCES app_users(id),
    transaction_reference VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    bill_id BIGINT NOT NULL REFERENCES bills(id),
    message TEXT NOT NULL,
    type VARCHAR(40) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION notify_after_bill_approved()
RETURNS TRIGGER AS $$
DECLARE
    customer_name TEXT;
BEGIN
    SELECT full_name INTO customer_name FROM customers WHERE id = NEW.customer_id;
    INSERT INTO notifications (customer_id, bill_id, message, type, is_read, created_at)
    VALUES (
        NEW.customer_id,
        NEW.id,
        'Dear ' || customer_name || ', Your ' || NEW.billing_month || '/' || NEW.billing_year ||
        ' utility bill of ' || NEW.total_amount || ' FRW has been successfully processed.',
        'BILL_GENERATED',
        FALSE,
        CURRENT_TIMESTAMP
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS after_bill_approved ON bills;
CREATE TRIGGER after_bill_approved
AFTER UPDATE OF status ON bills
FOR EACH ROW
WHEN (OLD.status IS DISTINCT FROM NEW.status AND NEW.status = 'APPROVED')
EXECUTE FUNCTION notify_after_bill_approved();

CREATE OR REPLACE FUNCTION notify_after_full_payment()
RETURNS TRIGGER AS $$
DECLARE
    customer_name TEXT;
BEGIN
    SELECT full_name INTO customer_name FROM customers WHERE id = NEW.customer_id;
    IF NEW.status <> 'PAID' THEN
        UPDATE bills SET status = 'PAID', updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id AND status <> 'PAID';
    END IF;
    INSERT INTO notifications (customer_id, bill_id, message, type, is_read, created_at)
    VALUES (
        NEW.customer_id,
        NEW.id,
        'Dear ' || customer_name || ', Your bill ' || NEW.bill_reference || ' has been fully paid. Thank you!',
        'PAYMENT_CONFIRMED',
        FALSE,
        CURRENT_TIMESTAMP
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS after_full_payment ON bills;
CREATE TRIGGER after_full_payment
AFTER UPDATE OF outstanding_balance ON bills
FOR EACH ROW
WHEN (OLD.outstanding_balance IS DISTINCT FROM NEW.outstanding_balance AND NEW.outstanding_balance = 0)
EXECUTE FUNCTION notify_after_full_payment();

INSERT INTO app_users (full_name, email, phone_number, password, status, role, email_verified)
VALUES ('Jolie Princesse Ishimwe', 'jolieprincesseishimwe@gmail.com', '+250785060644', 'UPDATED_BY_DATA_INITIALIZER', 'ACTIVE', 'ROLE_ADMIN', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO customers (full_name, national_id, email, phone_number, address, status)
VALUES
('Jean Doe', '1198000000000001', 'jean.doe@example.rw', '+250788111111', 'Kigali, Gasabo', 'ACTIVE'),
('Aline Uwase', '1198000000000002', 'aline.uwase@example.rw', '+250788222222', 'Kigali, Kicukiro', 'ACTIVE')
ON CONFLICT (national_id) DO NOTHING;

INSERT INTO meters (meter_number, meter_type, installation_date, status, customer_id)
SELECT 'WTR-0001', 'WATER', DATE '2025-01-01', 'ACTIVE', id FROM customers WHERE national_id = '1198000000000001'
ON CONFLICT (meter_number) DO NOTHING;

INSERT INTO meters (meter_number, meter_type, installation_date, status, customer_id)
SELECT 'ELE-0001', 'ELECTRICITY', DATE '2025-01-01', 'ACTIVE', id FROM customers WHERE national_id = '1198000000000002'
ON CONFLICT (meter_number) DO NOTHING;

INSERT INTO tariffs (name, meter_type, tariff_type, price_per_unit, fixed_charge, vat_percent, late_penalty_percent, effective_date, is_active, version)
SELECT 'Standard Water Tariff', 'WATER', 'FLAT', 500, 200, 18, 5, DATE '2025-01-01', TRUE, 1
WHERE NOT EXISTS (SELECT 1 FROM tariffs WHERE meter_type = 'WATER' AND is_active = TRUE);

INSERT INTO tariffs (name, meter_type, tariff_type, price_per_unit, fixed_charge, vat_percent, late_penalty_percent, effective_date, is_active, version)
SELECT 'Standard Electricity Tariff', 'ELECTRICITY', 'FLAT', 300, 500, 18, 5, DATE '2025-01-01', TRUE, 1
WHERE NOT EXISTS (SELECT 1 FROM tariffs WHERE meter_type = 'ELECTRICITY' AND is_active = TRUE);
