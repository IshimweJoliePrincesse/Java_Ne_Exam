package com.utility.billing.service;

import com.utility.billing.dto.MeterReadingDtos.*;

import java.util.List;

public interface MeterReadingService {
    MeterReadingResponse create(MeterReadingRequest request);
    List<MeterReadingResponse> findAll();
    MeterReadingResponse findById(Long id);
    List<MeterReadingResponse> findByMeter(Long meterId);
}
