package com.utility.billing.service;

import com.utility.billing.dto.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationResponse> findByCustomer(UUID customerId);
    NotificationResponse markRead(UUID id);
}
