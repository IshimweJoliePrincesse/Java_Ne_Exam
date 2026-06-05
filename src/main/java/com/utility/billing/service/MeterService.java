package com.utility.billing.service;

import com.utility.billing.dto.CustomerDtos.StatusRequest;
import com.utility.billing.dto.MeterDtos.*;

import java.util.List;
import java.util.UUID;

public interface MeterService {
    MeterResponse create(MeterRequest request);
    List<MeterResponse> findAll();
    List<MeterResponse> findByCustomer(UUID customerId);
    MeterResponse updateStatus(UUID id, StatusRequest request);
}
