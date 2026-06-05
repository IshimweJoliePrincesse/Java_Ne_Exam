package com.utility.billing.repository;

import com.utility.billing.entity.Tariff;
import com.utility.billing.enums.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TariffRepository extends JpaRepository<Tariff, UUID> {
    List<Tariff> findByIsActiveTrue();
    List<Tariff> findByMeterTypeOrderByVersionDesc(MeterType meterType);
    Optional<Tariff> findFirstByMeterTypeAndIsActiveTrueAndEffectiveDateLessThanEqualOrderByVersionDesc(MeterType meterType, LocalDate date);
}
