package com.utility.billing.service.impl;

import com.utility.billing.dto.NotificationResponse;
import com.utility.billing.entity.Notification;
import com.utility.billing.enums.Role;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.exception.UnauthorizedException;
import com.utility.billing.mapper.UtilityMappers;
import com.utility.billing.repository.NotificationRepository;
import com.utility.billing.service.NotificationService;
import com.utility.billing.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UtilityMappers mapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> findByCustomer(UUID customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream().map(mapper::toNotificationResponse).toList();
    }

    @Override
    public NotificationResponse markRead(UUID id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        var user = SecurityUtils.currentUser();
        if (user.getRole() == Role.ROLE_CUSTOMER && !notification.getCustomer().getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new UnauthorizedException("Customers can only update their own notifications");
        }
        notification.setIsRead(true);
        return mapper.toNotificationResponse(notification);
    }
}
