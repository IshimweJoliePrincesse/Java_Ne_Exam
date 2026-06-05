package com.utility.billing.repository;

import com.utility.billing.entity.Meter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MeterRepository extends JpaRepository<Meter, UUID> {
    boolean existsByMeterNumber(String meterNumber);
    List<Meter> findByCustomerId(UUID customerId);
}
