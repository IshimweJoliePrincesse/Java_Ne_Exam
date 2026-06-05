package com.utility.billing.service;

import com.utility.billing.dto.PaymentDtos.*;

import java.util.List;

public interface PaymentService {
    PaymentResponse record(PaymentRequest request);
    List<PaymentResponse> findAll();
    List<PaymentResponse> findByBill(Long billId);
    List<PaymentResponse> findByCustomer(Long customerId);
}
