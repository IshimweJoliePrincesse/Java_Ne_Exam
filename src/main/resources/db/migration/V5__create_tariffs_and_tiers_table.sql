-- Creates versioned tariff configurations and optional tier rows for tier-based pricing.
CREATE TABLE tariffs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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

CREATE TABLE tariff_tiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tariff_id UUID NOT NULL REFERENCES tariffs(id) ON DELETE CASCADE,
    min_units DOUBLE PRECISION NOT NULL,
    max_units DOUBLE PRECISION,
    price_per_unit DOUBLE PRECISION NOT NULL
);
