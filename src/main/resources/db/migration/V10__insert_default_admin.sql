-- Seeds the required admin account, sample staff/customer records, meters, and active tariffs.
INSERT INTO app_users (full_name, email, phone_number, password, status, role, email_verified)
VALUES
('Jolie Princesse Ishimwe', 'jolieprincesseishimwe@gmail.com', '+250785060644', 'UPDATED_BY_DATA_INITIALIZER', 'ACTIVE', 'ROLE_ADMIN', TRUE),
('Default Operator', 'operator@utility.rw', '+250780000001', 'UPDATED_BY_DATA_INITIALIZER', 'ACTIVE', 'ROLE_OPERATOR', TRUE),
('Default Finance', 'finance@utility.rw', '+250780000002', 'UPDATED_BY_DATA_INITIALIZER', 'ACTIVE', 'ROLE_FINANCE', TRUE)
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
VALUES
('Standard Water Tariff', 'WATER', 'FLAT', 500, 200, 18, 5, DATE '2025-01-01', TRUE, 1),
('Standard Electricity Tariff', 'ELECTRICITY', 'FLAT', 300, 500, 18, 5, DATE '2025-01-01', TRUE, 1);
