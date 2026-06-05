package com.utility.billing.repository;

import com.utility.billing.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    boolean existsByMeterReadingId(Long meterReadingId);
    Optional<Bill> findByBillReference(String billReference);
    List<Bill> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
