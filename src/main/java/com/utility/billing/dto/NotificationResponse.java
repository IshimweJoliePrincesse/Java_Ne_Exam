package com.utility.billing.dto;

import com.utility.billing.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(Long id, Long customerId, Long billId, String message, NotificationType type, Boolean isRead, LocalDateTime createdAt) {}
