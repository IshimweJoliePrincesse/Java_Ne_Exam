-- Creates generated bills and tracks approval, paid amounts, and outstanding balances.
CREATE TABLE bills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_reference VARCHAR(40) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customers(id),
    meter_id UUID NOT NULL REFERENCES meters(id),
    meter_reading_id UUID NOT NULL UNIQUE REFERENCES meter_readings(id),
    tariff_id UUID NOT NULL REFERENCES tariffs(id),
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
    approved_by UUID REFERENCES app_users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
