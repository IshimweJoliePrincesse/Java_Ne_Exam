package com.utility.billing.service.impl;

import com.utility.billing.dto.CustomerDtos.StatusRequest;
import com.utility.billing.dto.MeterDtos.MeterRequest;
import com.utility.billing.dto.MeterDtos.MeterResponse;
import com.utility.billing.entity.Customer;
import com.utility.billing.entity.Meter;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.UtilityMappers;
import com.utility.billing.repository.CustomerRepository;
import com.utility.billing.repository.MeterRepository;
import com.utility.billing.service.MeterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeterServiceImpl implements MeterService {
    private final MeterRepository meterRepository;
    private final CustomerRepository customerRepository;
    private final UtilityMappers mapper;

    @Override
    public MeterResponse create(MeterRequest request) {
        if (meterRepository.existsByMeterNumber(request.meterNumber())) {
            throw new DuplicateResourceException("Meter number already exists");
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Meter meter = Meter.builder()
                .meterNumber(request.meterNumber())
                .meterType(request.meterType())
                .installationDate(request.installationDate())
                .customer(customer)
                .build();
        return mapper.toMeterResponse(meterRepository.save(meter));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeterResponse> findAll() {
        return meterRepository.findAll().stream().map(mapper::toMeterResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeterResponse> findByCustomer(Long customerId) {
        return meterRepository.findByCustomerId(customerId)
                .stream()
                .map(mapper::toMeterResponse)
                .toList();
    }

    @Override
    public MeterResponse updateStatus(Long id, StatusRequest request) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found"));
        meter.setStatus(request.status());
        return mapper.toMeterResponse(meter);
    }
}
