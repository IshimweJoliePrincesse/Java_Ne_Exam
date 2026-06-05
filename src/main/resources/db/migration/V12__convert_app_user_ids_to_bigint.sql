-- Converts app_users primary keys and user foreign keys from UUID to BIGINT integer IDs.
ALTER TABLE meter_readings DROP CONSTRAINT IF EXISTS meter_readings_recorded_by_fkey;
ALTER TABLE bills DROP CONSTRAINT IF EXISTS bills_approved_by_fkey;
ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_recorded_by_fkey;

ALTER TABLE app_users ADD COLUMN IF NOT EXISTS numeric_id BIGINT;

WITH numbered_users AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY created_at, email) AS new_id
    FROM app_users
)
UPDATE app_users users
SET numeric_id = numbered_users.new_id
FROM numbered_users
WHERE users.id = numbered_users.id
  AND users.numeric_id IS NULL;

ALTER TABLE meter_readings ADD COLUMN IF NOT EXISTS recorded_by_numeric BIGINT;
UPDATE meter_readings readings
SET recorded_by_numeric = users.numeric_id
FROM app_users users
WHERE readings.recorded_by = users.id
  AND readings.recorded_by_numeric IS NULL;

ALTER TABLE bills ADD COLUMN IF NOT EXISTS approved_by_numeric BIGINT;
UPDATE bills bill_rows
SET approved_by_numeric = users.numeric_id
FROM app_users users
WHERE bill_rows.approved_by = users.id
  AND bill_rows.approved_by_numeric IS NULL;

ALTER TABLE payments ADD COLUMN IF NOT EXISTS recorded_by_numeric BIGINT;
UPDATE payments payment_rows
SET recorded_by_numeric = users.numeric_id
FROM app_users users
WHERE payment_rows.recorded_by = users.id
  AND payment_rows.recorded_by_numeric IS NULL;

ALTER TABLE meter_readings DROP COLUMN recorded_by;
ALTER TABLE meter_readings RENAME COLUMN recorded_by_numeric TO recorded_by;
ALTER TABLE meter_readings ALTER COLUMN recorded_by SET NOT NULL;

ALTER TABLE bills DROP COLUMN approved_by;
ALTER TABLE bills RENAME COLUMN approved_by_numeric TO approved_by;

ALTER TABLE payments DROP COLUMN recorded_by;
ALTER TABLE payments RENAME COLUMN recorded_by_numeric TO recorded_by;
ALTER TABLE payments ALTER COLUMN recorded_by SET NOT NULL;

ALTER TABLE app_users DROP CONSTRAINT IF EXISTS app_users_pkey;
ALTER TABLE app_users DROP COLUMN id;
ALTER TABLE app_users RENAME COLUMN numeric_id TO id;
ALTER TABLE app_users ALTER COLUMN id SET NOT NULL;
ALTER TABLE app_users ADD CONSTRAINT app_users_pkey PRIMARY KEY (id);

CREATE SEQUENCE IF NOT EXISTS app_users_id_seq;
SELECT setval('app_users_id_seq', COALESCE((SELECT MAX(id) FROM app_users), 0) + 1, false);
ALTER TABLE app_users ALTER COLUMN id SET DEFAULT nextval('app_users_id_seq');
ALTER SEQUENCE app_users_id_seq OWNED BY app_users.id;

ALTER TABLE meter_readings
    ADD CONSTRAINT meter_readings_recorded_by_fkey
    FOREIGN KEY (recorded_by) REFERENCES app_users(id);

ALTER TABLE bills
    ADD CONSTRAINT bills_approved_by_fkey
    FOREIGN KEY (approved_by) REFERENCES app_users(id);

ALTER TABLE payments
    ADD CONSTRAINT payments_recorded_by_fkey
    FOREIGN KEY (recorded_by) REFERENCES app_users(id);
