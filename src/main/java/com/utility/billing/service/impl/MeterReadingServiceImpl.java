package com.utility.billing.service.impl;

import com.utility.billing.dto.MeterReadingDtos.MeterReadingRequest;
import com.utility.billing.dto.MeterReadingDtos.MeterReadingResponse;
import com.utility.billing.entity.Meter;
import com.utility.billing.entity.MeterReading;
import com.utility.billing.enums.Status;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.InvalidOperationException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.UtilityMappers;
import com.utility.billing.repository.MeterReadingRepository;
import com.utility.billing.repository.MeterRepository;
import com.utility.billing.service.MeterReadingService;
import com.utility.billing.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeterReadingServiceImpl implements MeterReadingService {
    private final MeterReadingRepository readingRepository;
    private final MeterRepository meterRepository;
    private final UtilityMappers mapper;

    @Override
    public MeterReadingResponse create(MeterReadingRequest request) {
        if (request.currentReading() <= request.previousReading()) {
            throw new InvalidOperationException("Current reading must be greater than previous reading");
        }

        Meter meter = meterRepository.findById(request.meterId())
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found"));
        if (meter.getStatus() != Status.ACTIVE) {
            throw new InvalidOperationException("Meter must be active to accept readings");
        }

        int month = request.readingDate().getMonthValue();
        int year = request.readingDate().getYear();
        if (readingRepository.existsByMeterIdAndMonthAndYear(meter.getId(), month, year)) {
            throw new DuplicateResourceException("A reading already exists for this meter and month/year");
        }

        MeterReading reading = MeterReading.builder()
                .meter(meter)
                .previousReading(request.previousReading())
                .currentReading(request.currentReading())
                .consumption(request.currentReading() - request.previousReading())
                .readingDate(request.readingDate())
                .month(month)
                .year(year)
                .recordedBy(SecurityUtils.currentUser())
                .build();
        return mapper.toReadingResponse(readingRepository.save(reading));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeterReadingResponse> findAll() {
        return readingRepository.findAll().stream().map(mapper::toReadingResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MeterReadingResponse findById(Long id) {
        return mapper.toReadingResponse(readingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter reading not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeterReadingResponse> findByMeter(Long meterId) {
        return readingRepository.findByMeterIdOrderByReadingDateDesc(meterId)
                .stream()
                .map(mapper::toReadingResponse)
                .toList();
    }
}
