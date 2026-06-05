package com.utility.billing.controller;

import com.utility.billing.dto.MeterReadingDtos.*;
import com.utility.billing.service.MeterReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meter-readings")
@RequiredArgsConstructor
@Tag(name = "5. Meter Readings", description = "Capture and review monthly meter readings used to calculate customer consumption.")
public class MeterReadingController {
    private final MeterReadingService meterReadingService;

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(summary = "Capture meter reading", description = "Operator records a monthly reading. Current reading must be greater than previous reading, and only one reading is allowed per meter/month/year.")
    public MeterReadingResponse create(@Valid @RequestBody MeterReadingRequest request) {
        return meterReadingService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List meter readings", description = "Admin and finance users can review all captured readings.")
    public List<MeterReadingResponse> findAll() {
        return meterReadingService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','OPERATOR')")
    @Operation(summary = "Get reading by ID", description = "Returns a single meter reading by UUID.")
    public MeterReadingResponse findById(@PathVariable UUID id) {
        return meterReadingService.findById(id);
    }

    @GetMapping("/meter/{meterId}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','OPERATOR')")
    @Operation(summary = "List readings by meter", description = "Returns reading history for one meter, newest first.")
    public List<MeterReadingResponse> findByMeter(@PathVariable UUID meterId) {
        return meterReadingService.findByMeter(meterId);
    }
}
