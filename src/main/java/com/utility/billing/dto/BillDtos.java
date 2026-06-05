package com.utility.billing.dto;

import com.utility.billing.enums.BillStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class BillDtos {
    public record BillResponse(UUID id, String billReference, UUID customerId, String customerName, UUID meterId, String meterNumber, UUID meterReadingId, Double consumption, Double unitCharge, Double fixedCharge, Double vatAmount, Double penaltyAmount, Double totalAmount, Double amountPaid, Double outstandingBalance, Integer billingMonth, Integer billingYear, BillStatus status, LocalDate dueDate, Long approvedById, String approvedByName, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record BillSummaryResponse(UUID id, String billReference, String customerName, Double totalAmount, Double outstandingBalance, Integer billingMonth, Integer billingYear, BillStatus status) {}
}
