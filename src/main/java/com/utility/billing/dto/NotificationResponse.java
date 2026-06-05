package com.utility.billing.dto;

import com.utility.billing.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(UUID id, UUID customerId, UUID billId, String message, NotificationType type, Boolean isRead, LocalDateTime createdAt) {}
