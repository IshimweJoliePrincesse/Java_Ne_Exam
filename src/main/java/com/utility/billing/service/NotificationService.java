package com.utility.billing.service;

import com.utility.billing.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> findByCustomer(Long customerId);
    NotificationResponse markRead(Long id);
}
