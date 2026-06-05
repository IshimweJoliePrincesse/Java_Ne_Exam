package com.utility.billing.dto;

import com.utility.billing.enums.MeterType;
import com.utility.billing.enums.TariffType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TariffDtos {
    public record TariffTierRequest(@NotNull @PositiveOrZero Double minUnits, @PositiveOrZero Double maxUnits, @NotNull @Positive Double pricePerUnit) {}
    public record TariffTierResponse(Long id, Double minUnits, Double maxUnits, Double pricePerUnit) {}
    public record TariffRequest(
            @NotBlank @Size(min = 3, max = 120) String name,
            @NotNull MeterType meterType,
            @NotNull TariffType tariffType,
            @NotNull @PositiveOrZero Double pricePerUnit,
            @NotNull @PositiveOrZero Double fixedCharge,
            @NotNull @DecimalMax(value = "100.0") @PositiveOrZero Double vatPercent,
            @NotNull @DecimalMax(value = "100.0") @PositiveOrZero Double latePenaltyPercent,
            @NotNull @FutureOrPresent LocalDate effectiveDate,
            @Valid List<TariffTierRequest> tiers) {}
    public record TariffResponse(Long id, String name, MeterType meterType, TariffType tariffType, Double pricePerUnit, Double fixedCharge, Double vatPercent, Double latePenaltyPercent, LocalDate effectiveDate, Boolean isActive, Integer version, LocalDateTime createdAt, List<TariffTierResponse> tiers) {}
}
