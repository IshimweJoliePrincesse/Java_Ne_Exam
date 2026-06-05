package com.utility.billing.service.impl;

import com.utility.billing.dto.PaymentDtos.PaymentRequest;
import com.utility.billing.dto.PaymentDtos.PaymentResponse;
import com.utility.billing.entity.Bill;
import com.utility.billing.entity.Payment;
import com.utility.billing.enums.BillStatus;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.InvalidOperationException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.UtilityMappers;
import com.utility.billing.repository.BillRepository;
import com.utility.billing.repository.PaymentRepository;
import com.utility.billing.service.PaymentService;
import com.utility.billing.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final UtilityMappers mapper;

    @Override
    public PaymentResponse record(PaymentRequest request) {
        if (paymentRepository.existsByTransactionReference(request.transactionReference())) {
            throw new DuplicateResourceException("Transaction reference already exists");
        }
        Bill bill = billRepository.findById(request.billId()).orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
        if (bill.getStatus() != BillStatus.APPROVED && bill.getStatus() != BillStatus.PARTIALLY_PAID) {
            throw new InvalidOperationException("Only APPROVED or PARTIALLY_PAID bills can be paid");
        }
        if (request.amountPaid() > bill.getOutstandingBalance()) {
            throw new InvalidOperationException("Payment cannot exceed outstanding balance");
        }
        bill.setAmountPaid(bill.getAmountPaid() + request.amountPaid());
        bill.setOutstandingBalance(bill.getOutstandingBalance() - request.amountPaid());
        bill.setStatus(bill.getOutstandingBalance() == 0 ? BillStatus.PAID : BillStatus.PARTIALLY_PAID);
        Payment payment = Payment.builder()
                .bill(bill)
                .amountPaid(request.amountPaid())
                .paymentMethod(request.paymentMethod())
                .paymentDate(request.paymentDate())
                .recordedBy(SecurityUtils.currentUser())
                .transactionReference(request.transactionReference())
                .build();
        return mapper.toPaymentResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll().stream().map(mapper::toPaymentResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findByBill(Long billId) {
        return paymentRepository.findByBillIdOrderByCreatedAtDesc(billId).stream().map(mapper::toPaymentResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findByCustomer(Long customerId) {
        return paymentRepository.findByBillCustomerIdOrderByCreatedAtDesc(customerId).stream().map(mapper::toPaymentResponse).toList();
    }
}
