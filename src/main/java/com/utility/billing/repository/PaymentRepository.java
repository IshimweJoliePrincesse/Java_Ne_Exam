package com.utility.billing.repository;

import com.utility.billing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    boolean existsByTransactionReference(String transactionReference);
    List<Payment> findByBillIdOrderByCreatedAtDesc(UUID billId);
    List<Payment> findByBillCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
