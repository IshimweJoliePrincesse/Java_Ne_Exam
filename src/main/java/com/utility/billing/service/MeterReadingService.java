package com.utility.billing.service;

import com.utility.billing.dto.MeterReadingDtos.*;

import java.util.List;
import java.util.UUID;

public interface MeterReadingService {
    MeterReadingResponse create(MeterReadingRequest request);
    List<MeterReadingResponse> findAll();
    MeterReadingResponse findById(UUID id);
    List<MeterReadingResponse> findByMeter(UUID meterId);
}
