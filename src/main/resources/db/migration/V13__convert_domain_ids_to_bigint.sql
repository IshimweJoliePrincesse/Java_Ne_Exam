-- Converts all remaining domain primary keys and foreign keys from UUID to BIGINT integer IDs.
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_bill_id_fkey;
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_customer_id_fkey;
ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_bill_id_fkey;
ALTER TABLE bills DROP CONSTRAINT IF EXISTS bills_customer_id_fkey;
ALTER TABLE bills DROP CONSTRAINT IF EXISTS bills_meter_id_fkey;
ALTER TABLE bills DROP CONSTRAINT IF EXISTS bills_meter_reading_id_fkey;
ALTER TABLE bills DROP CONSTRAINT IF EXISTS bills_tariff_id_fkey;
ALTER TABLE tariff_tiers DROP CONSTRAINT IF EXISTS tariff_tiers_tariff_id_fkey;
ALTER TABLE meter_readings DROP CONSTRAINT IF EXISTS meter_readings_meter_id_fkey;
ALTER TABLE meter_readings DROP CONSTRAINT IF EXISTS uk_meter_reading_month_year;
ALTER TABLE meters DROP CONSTRAINT IF EXISTS meters_customer_id_fkey;

ALTER TABLE customers ADD COLUMN IF NOT EXISTS numeric_id BIGINT;
ALTER TABLE meters ADD COLUMN IF NOT EXISTS numeric_id BIGINT;
ALTER TABLE meter_readings ADD COLUMN IF NOT EXISTS numeric_id BIGINT;
ALTER TABLE tariffs ADD COLUMN IF NOT EXISTS numeric_id BIGINT;
ALTER TABLE tariff_tiers ADD COLUMN IF NOT EXISTS numeric_id BIGINT;
ALTER TABLE bills ADD COLUMN IF NOT EXISTS numeric_id BIGINT;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS numeric_id BIGINT;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS numeric_id BIGINT;

WITH numbered AS (SELECT id, ROW_NUMBER() OVER (ORDER BY created_at, national_id) AS new_id FROM customers)
UPDATE customers t SET numeric_id = numbered.new_id FROM numbered WHERE t.id = numbered.id AND t.numeric_id IS NULL;

WITH numbered AS (SELECT id, ROW_NUMBER() OVER (ORDER BY meter_number) AS new_id FROM meters)
UPDATE meters t SET numeric_id = numbered.new_id FROM numbered WHERE t.id = numbered.id AND t.numeric_id IS NULL;

WITH numbered AS (SELECT id, ROW_NUMBER() OVER (ORDER BY created_at) AS new_id FROM meter_readings)
UPDATE meter_readings t SET numeric_id = numbered.new_id FROM numbered WHERE t.id = numbered.id AND t.numeric_id IS NULL;

WITH numbered AS (SELECT id, ROW_NUMBER() OVER (ORDER BY created_at, name) AS new_id FROM tariffs)
UPDATE tariffs t SET numeric_id = numbered.new_id FROM numbered WHERE t.id = numbered.id AND t.numeric_id IS NULL;

WITH numbered AS (SELECT id, ROW_NUMBER() OVER (ORDER BY min_units, price_per_unit) AS new_id FROM tariff_tiers)
UPDATE tariff_tiers t SET numeric_id = numbered.new_id FROM numbered WHERE t.id = numbered.id AND t.numeric_id IS NULL;

WITH numbered AS (SELECT id, ROW_NUMBER() OVER (ORDER BY created_at, bill_reference) AS new_id FROM bills)
UPDATE bills t SET numeric_id = numbered.new_id FROM numbered WHERE t.id = numbered.id AND t.numeric_id IS NULL;

WITH numbered AS (SELECT id, ROW_NUMBER() OVER (ORDER BY created_at, transaction_reference) AS new_id FROM payments)
UPDATE payments t SET numeric_id = numbered.new_id FROM numbered WHERE t.id = numbered.id AND t.numeric_id IS NULL;

WITH numbered AS (SELECT id, ROW_NUMBER() OVER (ORDER BY created_at) AS new_id FROM notifications)
UPDATE notifications t SET numeric_id = numbered.new_id FROM numbered WHERE t.id = numbered.id AND t.numeric_id IS NULL;

ALTER TABLE meters ADD COLUMN IF NOT EXISTS customer_id_numeric BIGINT;
UPDATE meters child SET customer_id_numeric = parent.numeric_id FROM customers parent WHERE child.customer_id = parent.id;

ALTER TABLE meter_readings ADD COLUMN IF NOT EXISTS meter_id_numeric BIGINT;
UPDATE meter_readings child SET meter_id_numeric = parent.numeric_id FROM meters parent WHERE child.meter_id = parent.id;

ALTER TABLE tariff_tiers ADD COLUMN IF NOT EXISTS tariff_id_numeric BIGINT;
UPDATE tariff_tiers child SET tariff_id_numeric = parent.numeric_id FROM tariffs parent WHERE child.tariff_id = parent.id;

ALTER TABLE bills ADD COLUMN IF NOT EXISTS customer_id_numeric BIGINT;
ALTER TABLE bills ADD COLUMN IF NOT EXISTS meter_id_numeric BIGINT;
ALTER TABLE bills ADD COLUMN IF NOT EXISTS meter_reading_id_numeric BIGINT;
ALTER TABLE bills ADD COLUMN IF NOT EXISTS tariff_id_numeric BIGINT;
UPDATE bills child SET customer_id_numeric = parent.numeric_id FROM customers parent WHERE child.customer_id = parent.id;
UPDATE bills child SET meter_id_numeric = parent.numeric_id FROM meters parent WHERE child.meter_id = parent.id;
UPDATE bills child SET meter_reading_id_numeric = parent.numeric_id FROM meter_readings parent WHERE child.meter_reading_id = parent.id;
UPDATE bills child SET tariff_id_numeric = parent.numeric_id FROM tariffs parent WHERE child.tariff_id = parent.id;

ALTER TABLE payments ADD COLUMN IF NOT EXISTS bill_id_numeric BIGINT;
UPDATE payments child SET bill_id_numeric = parent.numeric_id FROM bills parent WHERE child.bill_id = parent.id;

ALTER TABLE notifications ADD COLUMN IF NOT EXISTS customer_id_numeric BIGINT;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS bill_id_numeric BIGINT;
UPDATE notifications child SET customer_id_numeric = parent.numeric_id FROM customers parent WHERE child.customer_id = parent.id;
UPDATE notifications child SET bill_id_numeric = parent.numeric_id FROM bills parent WHERE child.bill_id = parent.id;

ALTER TABLE meters DROP COLUMN customer_id;
ALTER TABLE meters RENAME COLUMN customer_id_numeric TO customer_id;
ALTER TABLE meters ALTER COLUMN customer_id SET NOT NULL;

ALTER TABLE meter_readings DROP COLUMN meter_id;
ALTER TABLE meter_readings RENAME COLUMN meter_id_numeric TO meter_id;
ALTER TABLE meter_readings ALTER COLUMN meter_id SET NOT NULL;

ALTER TABLE tariff_tiers DROP COLUMN tariff_id;
ALTER TABLE tariff_tiers RENAME COLUMN tariff_id_numeric TO tariff_id;
ALTER TABLE tariff_tiers ALTER COLUMN tariff_id SET NOT NULL;

ALTER TABLE bills DROP COLUMN customer_id;
ALTER TABLE bills DROP COLUMN meter_id;
ALTER TABLE bills DROP COLUMN meter_reading_id;
ALTER TABLE bills DROP COLUMN tariff_id;
ALTER TABLE bills RENAME COLUMN customer_id_numeric TO customer_id;
ALTER TABLE bills RENAME COLUMN meter_id_numeric TO meter_id;
ALTER TABLE bills RENAME COLUMN meter_reading_id_numeric TO meter_reading_id;
ALTER TABLE bills RENAME COLUMN tariff_id_numeric TO tariff_id;
ALTER TABLE bills ALTER COLUMN customer_id SET NOT NULL;
ALTER TABLE bills ALTER COLUMN meter_id SET NOT NULL;
ALTER TABLE bills ALTER COLUMN meter_reading_id SET NOT NULL;
ALTER TABLE bills ALTER COLUMN tariff_id SET NOT NULL;

ALTER TABLE payments DROP COLUMN bill_id;
ALTER TABLE payments RENAME COLUMN bill_id_numeric TO bill_id;
ALTER TABLE payments ALTER COLUMN bill_id SET NOT NULL;

ALTER TABLE notifications DROP COLUMN customer_id;
ALTER TABLE notifications DROP COLUMN bill_id;
ALTER TABLE notifications RENAME COLUMN customer_id_numeric TO customer_id;
ALTER TABLE notifications RENAME COLUMN bill_id_numeric TO bill_id;
ALTER TABLE notifications ALTER COLUMN customer_id SET NOT NULL;
ALTER TABLE notifications ALTER COLUMN bill_id SET NOT NULL;

ALTER TABLE customers DROP CONSTRAINT IF EXISTS customers_pkey;
ALTER TABLE meters DROP CONSTRAINT IF EXISTS meters_pkey;
ALTER TABLE meter_readings DROP CONSTRAINT IF EXISTS meter_readings_pkey;
ALTER TABLE tariffs DROP CONSTRAINT IF EXISTS tariffs_pkey;
ALTER TABLE tariff_tiers DROP CONSTRAINT IF EXISTS tariff_tiers_pkey;
ALTER TABLE bills DROP CONSTRAINT IF EXISTS bills_pkey;
ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_pkey;
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_pkey;

ALTER TABLE customers DROP COLUMN id;
ALTER TABLE meters DROP COLUMN id;
ALTER TABLE meter_readings DROP COLUMN id;
ALTER TABLE tariffs DROP COLUMN id;
ALTER TABLE tariff_tiers DROP COLUMN id;
ALTER TABLE bills DROP COLUMN id;
ALTER TABLE payments DROP COLUMN id;
ALTER TABLE notifications DROP COLUMN id;

ALTER TABLE customers RENAME COLUMN numeric_id TO id;
ALTER TABLE meters RENAME COLUMN numeric_id TO id;
ALTER TABLE meter_readings RENAME COLUMN numeric_id TO id;
ALTER TABLE tariffs RENAME COLUMN numeric_id TO id;
ALTER TABLE tariff_tiers RENAME COLUMN numeric_id TO id;
ALTER TABLE bills RENAME COLUMN numeric_id TO id;
ALTER TABLE payments RENAME COLUMN numeric_id TO id;
ALTER TABLE notifications RENAME COLUMN numeric_id TO id;

CREATE SEQUENCE IF NOT EXISTS customers_id_seq;
CREATE SEQUENCE IF NOT EXISTS meters_id_seq;
CREATE SEQUENCE IF NOT EXISTS meter_readings_id_seq;
CREATE SEQUENCE IF NOT EXISTS tariffs_id_seq;
CREATE SEQUENCE IF NOT EXISTS tariff_tiers_id_seq;
CREATE SEQUENCE IF NOT EXISTS bills_id_seq;
CREATE SEQUENCE IF NOT EXISTS payments_id_seq;
CREATE SEQUENCE IF NOT EXISTS notifications_id_seq;

ALTER TABLE customers ALTER COLUMN id SET NOT NULL;
ALTER TABLE meters ALTER COLUMN id SET NOT NULL;
ALTER TABLE meter_readings ALTER COLUMN id SET NOT NULL;
ALTER TABLE tariffs ALTER COLUMN id SET NOT NULL;
ALTER TABLE tariff_tiers ALTER COLUMN id SET NOT NULL;
ALTER TABLE bills ALTER COLUMN id SET NOT NULL;
ALTER TABLE payments ALTER COLUMN id SET NOT NULL;
ALTER TABLE notifications ALTER COLUMN id SET NOT NULL;

ALTER TABLE customers ADD CONSTRAINT customers_pkey PRIMARY KEY (id);
ALTER TABLE meters ADD CONSTRAINT meters_pkey PRIMARY KEY (id);
ALTER TABLE meter_readings ADD CONSTRAINT meter_readings_pkey PRIMARY KEY (id);
ALTER TABLE tariffs ADD CONSTRAINT tariffs_pkey PRIMARY KEY (id);
ALTER TABLE tariff_tiers ADD CONSTRAINT tariff_tiers_pkey PRIMARY KEY (id);
ALTER TABLE bills ADD CONSTRAINT bills_pkey PRIMARY KEY (id);
ALTER TABLE payments ADD CONSTRAINT payments_pkey PRIMARY KEY (id);
ALTER TABLE notifications ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);

SELECT setval('customers_id_seq', COALESCE((SELECT MAX(id) FROM customers), 0) + 1, false);
SELECT setval('meters_id_seq', COALESCE((SELECT MAX(id) FROM meters), 0) + 1, false);
SELECT setval('meter_readings_id_seq', COALESCE((SELECT MAX(id) FROM meter_readings), 0) + 1, false);
SELECT setval('tariffs_id_seq', COALESCE((SELECT MAX(id) FROM tariffs), 0) + 1, false);
SELECT setval('tariff_tiers_id_seq', COALESCE((SELECT MAX(id) FROM tariff_tiers), 0) + 1, false);
SELECT setval('bills_id_seq', COALESCE((SELECT MAX(id) FROM bills), 0) + 1, false);
SELECT setval('payments_id_seq', COALESCE((SELECT MAX(id) FROM payments), 0) + 1, false);
SELECT setval('notifications_id_seq', COALESCE((SELECT MAX(id) FROM notifications), 0) + 1, false);

ALTER TABLE customers ALTER COLUMN id SET DEFAULT nextval('customers_id_seq');
ALTER TABLE meters ALTER COLUMN id SET DEFAULT nextval('meters_id_seq');
ALTER TABLE meter_readings ALTER COLUMN id SET DEFAULT nextval('meter_readings_id_seq');
ALTER TABLE tariffs ALTER COLUMN id SET DEFAULT nextval('tariffs_id_seq');
ALTER TABLE tariff_tiers ALTER COLUMN id SET DEFAULT nextval('tariff_tiers_id_seq');
ALTER TABLE bills ALTER COLUMN id SET DEFAULT nextval('bills_id_seq');
ALTER TABLE payments ALTER COLUMN id SET DEFAULT nextval('payments_id_seq');
ALTER TABLE notifications ALTER COLUMN id SET DEFAULT nextval('notifications_id_seq');

ALTER TABLE meters ADD CONSTRAINT meters_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customers(id);
ALTER TABLE meter_readings ADD CONSTRAINT meter_readings_meter_id_fkey FOREIGN KEY (meter_id) REFERENCES meters(id);
ALTER TABLE meter_readings ADD CONSTRAINT uk_meter_reading_month_year UNIQUE (meter_id, month, year);
ALTER TABLE tariff_tiers ADD CONSTRAINT tariff_tiers_tariff_id_fkey FOREIGN KEY (tariff_id) REFERENCES tariffs(id) ON DELETE CASCADE;
ALTER TABLE bills ADD CONSTRAINT bills_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customers(id);
ALTER TABLE bills ADD CONSTRAINT bills_meter_id_fkey FOREIGN KEY (meter_id) REFERENCES meters(id);
ALTER TABLE bills ADD CONSTRAINT bills_meter_reading_id_fkey FOREIGN KEY (meter_reading_id) REFERENCES meter_readings(id);
ALTER TABLE bills ADD CONSTRAINT bills_tariff_id_fkey FOREIGN KEY (tariff_id) REFERENCES tariffs(id);
ALTER TABLE payments ADD CONSTRAINT payments_bill_id_fkey FOREIGN KEY (bill_id) REFERENCES bills(id);
ALTER TABLE notifications ADD CONSTRAINT notifications_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customers(id);
ALTER TABLE notifications ADD CONSTRAINT notifications_bill_id_fkey FOREIGN KEY (bill_id) REFERENCES bills(id);
