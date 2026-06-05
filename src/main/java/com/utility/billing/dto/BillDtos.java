package com.utility.billing.dto;

import com.utility.billing.enums.BillStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BillDtos {
    public record BillResponse(Long id, String billReference, Long customerId, String customerName, Long meterId, String meterNumber, Long meterReadingId, Double consumption, Double unitCharge, Double fixedCharge, Double vatAmount, Double penaltyAmount, Double totalAmount, Double amountPaid, Double outstandingBalance, Integer billingMonth, Integer billingYear, BillStatus status, LocalDate dueDate, Long approvedById, String approvedByName, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record BillSummaryResponse(Long id, String billReference, String customerName, Double totalAmount, Double outstandingBalance, Integer billingMonth, Integer billingYear, BillStatus status) {}
}
