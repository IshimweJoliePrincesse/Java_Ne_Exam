package com.utility.billing.service;

import com.utility.billing.dto.PaymentDtos.*;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse record(PaymentRequest request);
    List<PaymentResponse> findAll();
    List<PaymentResponse> findByBill(UUID billId);
    List<PaymentResponse> findByCustomer(UUID customerId);
}
