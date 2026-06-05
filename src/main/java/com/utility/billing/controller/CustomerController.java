package com.utility.billing.controller;

import com.utility.billing.dto.CustomerDtos.*;
import com.utility.billing.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "3. Customers", description = "Admins/operators manage customer records. Customer users can view and update their own profile.")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @Operation(summary = "List customers", description = "Returns customers with optional search by name, National ID, or phone number.")
    public List<CustomerResponse> findAll(@RequestParam(required = false) String search) {
        return customerService.findAll(search);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get my customer profile", description = "Customer returns their own profile using the email from the authenticated account.")
    public CustomerResponse findOwnProfile() {
        return customerService.findOwnProfile();
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update my customer profile", description = "Customer updates their own name, phone number, and address.")
    public CustomerResponse updateOwnProfile(@Valid @RequestBody CustomerProfileUpdateRequest request) {
        return customerService.updateOwnProfile(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @Operation(summary = "Get customer by ID", description = "Returns a single customer by Long.")
    public CustomerResponse findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @Operation(summary = "Update customer", description = "Updates customer contact and address information.")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return customerService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate or deactivate customer", description = "Admin changes customer status. Deactivation also disables the matching user login and all meters owned by the customer.")
    public CustomerResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return customerService.updateStatus(id, request);
    }
}
