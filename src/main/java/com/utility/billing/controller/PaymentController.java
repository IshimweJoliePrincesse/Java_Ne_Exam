package com.utility.billing.controller;

import com.utility.billing.dto.PaymentDtos.*;
import com.utility.billing.enums.Role;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.exception.UnauthorizedException;
import com.utility.billing.repository.CustomerRepository;
import com.utility.billing.service.PaymentService;
import com.utility.billing.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "8. Payments", description = "Record customer payments and review bill/customer payment history.")
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomerRepository customerRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "Record payment", description = "Finance/admin records a partial or full payment. Full payment marks the bill as PAID and triggers a notification.")
    public PaymentResponse record(@Valid @RequestBody PaymentRequest request) {
        return paymentService.record(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List payments", description = "Returns all payment records.")
    public List<PaymentResponse> findAll() {
        return paymentService.findAll();
    }

    @GetMapping("/bill/{billId}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List payments by bill", description = "Returns all payments recorded for a specific bill.")
    public List<PaymentResponse> findByBill(@PathVariable UUID billId) {
        return paymentService.findByBill(billId);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','CUSTOMER')")
    @Operation(summary = "List customer payment history", description = "Returns payment history for a customer. Customer users can only access their own payments.")
    public List<PaymentResponse> findByCustomer(@PathVariable UUID customerId) {
        ensureOwnCustomerIfCustomer(customerId);
        return paymentService.findByCustomer(customerId);
    }

    private void ensureOwnCustomerIfCustomer(UUID customerId) {
        var user = SecurityUtils.currentUser();
        if (user.getRole() == Role.ROLE_CUSTOMER) {
            var customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            if (!customer.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new UnauthorizedException("Customers can only access their own payments");
            }
        }
    }
}
