package com.utility.billing.repository;

import com.utility.billing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByTransactionReference(String transactionReference);
    List<Payment> findByBillIdOrderByCreatedAtDesc(Long billId);
    List<Payment> findByBillCustomerIdOrderByCreatedAtDesc(Long customerId);
}
