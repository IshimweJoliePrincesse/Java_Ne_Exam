package com.utility.billing.service;

import com.utility.billing.dto.TariffDtos.*;

import java.util.List;
import java.util.UUID;

public interface TariffService {
    TariffResponse create(TariffRequest request);
    List<TariffResponse> findAll();
    List<TariffResponse> findActive();
    TariffResponse update(UUID id, TariffRequest request);
}
