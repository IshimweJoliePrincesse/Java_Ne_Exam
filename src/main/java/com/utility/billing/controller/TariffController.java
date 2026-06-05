package com.utility.billing.controller;

import com.utility.billing.dto.TariffDtos.*;
import com.utility.billing.service.TariffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tariffs")
@RequiredArgsConstructor
@Tag(name = "6. Tariffs", description = "Configure versioned water/electricity pricing, fixed charges, VAT, penalties, and tariff tiers.")
public class TariffController {
    private final TariffService tariffService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create tariff", description = "Admin creates a new active tariff version for water or electricity. Older tariffs for the same meter type are deactivated.")
    public TariffResponse create(@Valid @RequestBody TariffRequest request) {
        return tariffService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List all tariffs", description = "Returns every tariff version, active and inactive.")
    public List<TariffResponse> findAll() {
        return tariffService.findAll();
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @Operation(summary = "List active tariffs", description = "Returns currently active tariffs used for future billing cycles.")
    public List<TariffResponse> findActive() {
        return tariffService.findActive();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new tariff version", description = "Updates a tariff by creating a new version and deactivating the old one.")
    public TariffResponse update(@PathVariable UUID id, @Valid @RequestBody TariffRequest request) {
        return tariffService.update(id, request);
    }
}
