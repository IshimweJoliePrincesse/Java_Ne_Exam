package com.utility.billing.dto;

import com.utility.billing.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentDtos {
    public record PaymentRequest(
            @NotNull Long billId,
            @NotNull @Positive Double amountPaid,
            @NotNull PaymentMethod paymentMethod,
            @NotNull @PastOrPresent LocalDate paymentDate,
            @NotBlank @Size(min = 4, max = 120) @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Transaction reference can contain letters, numbers, and hyphens only") String transactionReference) {}
    public record PaymentResponse(Long id, Long billId, String billReference, Double amountPaid, PaymentMethod paymentMethod, LocalDate paymentDate, Long recordedById, String recordedByName, String transactionReference, LocalDateTime createdAt) {}
}
