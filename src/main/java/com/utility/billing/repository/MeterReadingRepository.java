package com.utility.billing.repository;

import com.utility.billing.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    boolean existsByMeterIdAndMonthAndYear(Long meterId, Integer month, Integer year);
    List<MeterReading> findByMeterIdOrderByReadingDateDesc(Long meterId);
}
