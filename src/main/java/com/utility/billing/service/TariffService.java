package com.utility.billing.service;

import com.utility.billing.dto.TariffDtos.*;

import java.util.List;

public interface TariffService {
    TariffResponse create(TariffRequest request);
    List<TariffResponse> findAll();
    List<TariffResponse> findActive();
    TariffResponse update(Long id, TariffRequest request);
}
