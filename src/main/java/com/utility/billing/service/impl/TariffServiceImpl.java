package com.utility.billing.service.impl;

import com.utility.billing.dto.TariffDtos.TariffRequest;
import com.utility.billing.dto.TariffDtos.TariffResponse;
import com.utility.billing.entity.Tariff;
import com.utility.billing.entity.TariffTier;
import com.utility.billing.enums.TariffType;
import com.utility.billing.exception.InvalidOperationException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.UtilityMappers;
import com.utility.billing.repository.TariffRepository;
import com.utility.billing.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TariffServiceImpl implements TariffService {
    private final TariffRepository tariffRepository;
    private final UtilityMappers mapper;

    @Override
    public TariffResponse create(TariffRequest request) {
        validateTiers(request);
        int nextVersion = tariffRepository.findByMeterTypeOrderByVersionDesc(request.meterType()).stream()
                .findFirst().map(t -> t.getVersion() + 1).orElse(1);
        tariffRepository.findByMeterTypeOrderByVersionDesc(request.meterType()).forEach(t -> t.setIsActive(false));
        return mapper.toTariffResponse(tariffRepository.save(buildTariff(request, nextVersion)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TariffResponse> findAll() {
        return tariffRepository.findAll().stream().map(mapper::toTariffResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TariffResponse> findActive() {
        return tariffRepository.findByIsActiveTrue().stream().map(mapper::toTariffResponse).toList();
    }

    @Override
    public TariffResponse update(Long id, TariffRequest request) {
        Tariff old = tariffRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tariff not found"));
        old.setIsActive(false);
        validateTiers(request);
        return mapper.toTariffResponse(tariffRepository.save(buildTariff(request, old.getVersion() + 1)));
    }

    private Tariff buildTariff(TariffRequest request, int version) {
        Tariff tariff = Tariff.builder()
                .name(request.name())
                .meterType(request.meterType())
                .tariffType(request.tariffType())
                .pricePerUnit(request.pricePerUnit())
                .fixedCharge(request.fixedCharge())
                .vatPercent(request.vatPercent())
                .latePenaltyPercent(request.latePenaltyPercent())
                .effectiveDate(request.effectiveDate())
                .isActive(true)
                .version(version)
                .build();
        if (request.tiers() != null) {
            request.tiers().forEach(t -> tariff.getTiers().add(TariffTier.builder()
                    .tariff(tariff)
                    .minUnits(t.minUnits())
                    .maxUnits(t.maxUnits())
                    .pricePerUnit(t.pricePerUnit())
                    .build()));
        }
        return tariff;
    }

    private void validateTiers(TariffRequest request) {
        if (request.tariffType() == TariffType.TIERED && (request.tiers() == null || request.tiers().isEmpty())) {
            throw new InvalidOperationException("Tiered tariffs require at least one tier");
        }
    }
}
