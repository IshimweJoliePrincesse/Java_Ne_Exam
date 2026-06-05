package com.utility.billing.controller;

import com.utility.billing.dto.BillDtos.*;
import com.utility.billing.enums.Role;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.exception.UnauthorizedException;
import com.utility.billing.repository.CustomerRepository;
import com.utility.billing.service.BillService;
import com.utility.billing.service.BillingPdfService;
import com.utility.billing.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Tag(name = "7. Bills", description = "Generate, approve, view, and download utility bills calculated from approved meter readings.")
public class BillController {
    private final BillService billService;
    private final BillingPdfService billingPdfService;
    private final CustomerRepository customerRepository;

    @PostMapping("/generate/{meterReadingId}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "Generate bill from reading", description = "Creates a pending bill from a meter reading using the active tariff for the meter type.")
    public BillResponse generate(@PathVariable Long meterReadingId) {
        return billService.generate(meterReadingId);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "Approve bill", description = "Finance/admin approves a pending bill. Database trigger creates a customer notification.")
    public BillResponse approve(@PathVariable Long id) {
        return billService.approve(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List bills", description = "Returns all bills for admin and finance users.")
    public List<BillSummaryResponse> findAll() {
        return billService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "Get bill by ID", description = "Returns complete bill details by Long.")
    public BillResponse findById(@PathVariable Long id) {
        return billService.findById(id);
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','CUSTOMER')")
    @Operation(summary = "Download bill PDF", description = "Downloads a styled PDF bill. Customers can only download their own bills.")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = billingPdfService.generateBillPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill-" + id + ".pdf")
                .body(pdf);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','CUSTOMER')")
    @Operation(summary = "List customer bills", description = "Returns bill history for a customer. Customer users can only access their own bill history.")
    public List<BillSummaryResponse> findByCustomer(@PathVariable Long customerId) {
        ensureOwnCustomerIfCustomer(customerId);
        return billService.findByCustomer(customerId);
    }

    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "Get bill by reference", description = "Finds a bill using its generated reference, for example BILL-202501-00123.")
    public BillResponse findByReference(@PathVariable String reference) {
        return billService.findByReference(reference);
    }

    private void ensureOwnCustomerIfCustomer(Long customerId) {
        var user = SecurityUtils.currentUser();
        if (user.getRole() == Role.ROLE_CUSTOMER) {
            var customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            if (!customer.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new UnauthorizedException("Customers can only access their own bills");
            }
        }
    }
}
