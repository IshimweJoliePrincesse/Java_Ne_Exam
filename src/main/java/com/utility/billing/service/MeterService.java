package com.utility.billing.service;

import com.utility.billing.dto.CustomerDtos.StatusRequest;
import com.utility.billing.dto.MeterDtos.*;

import java.util.List;

public interface MeterService {
    MeterResponse create(MeterRequest request);
    List<MeterResponse> findAll();
    List<MeterResponse> findByCustomer(Long customerId);
    MeterResponse updateStatus(Long id, StatusRequest request);
}
