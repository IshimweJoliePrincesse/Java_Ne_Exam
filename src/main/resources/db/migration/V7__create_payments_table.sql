-- Creates payment records tied to approved bills and finance/admin users.
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES bills(id),
    amount_paid DOUBLE PRECISION NOT NULL,
    payment_method VARCHAR(40) NOT NULL,
    payment_date DATE NOT NULL,
    recorded_by UUID NOT NULL REFERENCES app_users(id),
    transaction_reference VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
