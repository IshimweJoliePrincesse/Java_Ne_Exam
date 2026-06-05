package com.utility.billing.repository;

import com.utility.billing.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MeterReadingRepository extends JpaRepository<MeterReading, UUID> {
    boolean existsByMeterIdAndMonthAndYear(UUID meterId, Integer month, Integer year);
    List<MeterReading> findByMeterIdOrderByReadingDateDesc(UUID meterId);
}
