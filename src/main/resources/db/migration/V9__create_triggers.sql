-- Inserts a notification automatically when finance/admin approves a bill.
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

CREATE TRIGGER after_bill_approved
AFTER UPDATE OF status ON bills
FOR EACH ROW
WHEN (OLD.status IS DISTINCT FROM NEW.status AND NEW.status = 'APPROVED')
EXECUTE FUNCTION notify_after_bill_approved();

-- Confirms full payment by keeping the bill PAID and notifying the customer once the balance reaches zero.
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

CREATE TRIGGER after_full_payment
AFTER UPDATE OF outstanding_balance ON bills
FOR EACH ROW
WHEN (OLD.outstanding_balance IS DISTINCT FROM NEW.outstanding_balance AND NEW.outstanding_balance = 0)
EXECUTE FUNCTION notify_after_full_payment();
