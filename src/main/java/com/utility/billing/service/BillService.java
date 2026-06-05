package com.utility.billing.service;

import com.utility.billing.dto.BillDtos.*;

import java.util.List;

public interface BillService {
    BillResponse generate(Long meterReadingId);
    BillResponse approve(Long id);
    List<BillSummaryResponse> findAll();
    BillResponse findById(Long id);
    List<BillSummaryResponse> findByCustomer(Long customerId);
    BillResponse findByReference(String reference);
}
