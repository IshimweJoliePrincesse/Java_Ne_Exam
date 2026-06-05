-- Stores monthly meter readings and prevents duplicate readings per meter/month/year.
CREATE TABLE meter_readings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meter_id UUID NOT NULL REFERENCES meters(id),
    previous_reading DOUBLE PRECISION NOT NULL,
    current_reading DOUBLE PRECISION NOT NULL,
    consumption DOUBLE PRECISION NOT NULL,
    reading_date DATE NOT NULL,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    recorded_by UUID NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_meter_reading_month_year UNIQUE (meter_id, month, year)
);
