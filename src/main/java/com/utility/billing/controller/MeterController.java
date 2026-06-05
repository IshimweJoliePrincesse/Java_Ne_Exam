package com.utility.billing.controller;

import com.utility.billing.dto.CustomerDtos.StatusRequest;
import com.utility.billing.dto.MeterDtos.*;
import com.utility.billing.service.MeterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
@Tag(name = "4. Meters", description = "Register and manage physical water/electricity meters assigned to customers.")
@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
public class MeterController {
    private final MeterService meterService;

    @PostMapping
    @Operation(summary = "Assign meter to customer", description = "Registers a unique water or electricity meter and links it to an existing customer.")
    public MeterResponse create(@Valid @RequestBody MeterRequest request) {
        return meterService.create(request);
    }

    @GetMapping
    @Operation(summary = "List meters", description = "Returns all registered meters.")
    public List<MeterResponse> findAll() {
        return meterService.findAll();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List customer meters", description = "Returns all meters assigned to a specific customer.")
    public List<MeterResponse> findByCustomer(@PathVariable UUID customerId) {
        return meterService.findByCustomer(customerId);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate or deactivate meter", description = "Changes meter status. Inactive meters cannot receive new readings.")
    public MeterResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody StatusRequest request) {
        return meterService.updateStatus(id, request);
    }
}
