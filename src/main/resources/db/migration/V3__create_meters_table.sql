-- Creates utility meters and links each physical meter to a customer.
CREATE TABLE meters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meter_number VARCHAR(80) NOT NULL UNIQUE,
    meter_type VARCHAR(30) NOT NULL,
    installation_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    customer_id UUID NOT NULL REFERENCES customers(id)
);
