package com.utility.billing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MeterReadingDtos {
    public record MeterReadingRequest(
            @NotNull Long meterId,
            @NotNull @PositiveOrZero Double previousReading,
            @NotNull @PositiveOrZero Double currentReading,
            @NotNull @PastOrPresent LocalDate readingDate) {}
    public record MeterReadingResponse(Long id, Long meterId, String meterNumber, Double previousReading, Double currentReading, Double consumption, LocalDate readingDate, Integer month, Integer year, Long recordedById, String recordedByName, LocalDateTime createdAt) {}
}
