package com.utility.billing.repository;

import com.utility.billing.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends JpaRepository<Bill, UUID> {
    boolean existsByMeterReadingId(UUID meterReadingId);
    Optional<Bill> findByBillReference(String billReference);
    List<Bill> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
