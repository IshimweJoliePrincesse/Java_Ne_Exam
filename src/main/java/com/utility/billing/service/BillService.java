package com.utility.billing.service;

import com.utility.billing.dto.BillDtos.*;

import java.util.List;
import java.util.UUID;

public interface BillService {
    BillResponse generate(UUID meterReadingId);
    BillResponse approve(UUID id);
    List<BillSummaryResponse> findAll();
    BillResponse findById(UUID id);
    List<BillSummaryResponse> findByCustomer(UUID customerId);
    BillResponse findByReference(String reference);
}
