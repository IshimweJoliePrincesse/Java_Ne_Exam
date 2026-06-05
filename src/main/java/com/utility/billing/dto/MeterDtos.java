package com.utility.billing.dto;

import com.utility.billing.enums.MeterType;
import com.utility.billing.enums.Status;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class MeterDtos {
    public record MeterRequest(
            @NotBlank @Size(min = 3, max = 80) @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Meter number can contain letters, numbers, and hyphens only") String meterNumber,
            @NotNull MeterType meterType,
            @NotNull @PastOrPresent LocalDate installationDate,
            @NotNull Long customerId) {}
    public record MeterResponse(Long id, String meterNumber, MeterType meterType, LocalDate installationDate, Status status, Long customerId, String customerName) {}
}
