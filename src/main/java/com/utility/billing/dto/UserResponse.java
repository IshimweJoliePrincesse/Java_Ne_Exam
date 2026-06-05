package com.utility.billing.dto;

import com.utility.billing.enums.Role;
import com.utility.billing.enums.Status;

import java.time.LocalDateTime;

public record UserResponse(Long id, String fullName, String email, String phoneNumber, Status status, Role role, boolean emailVerified, LocalDateTime createdAt) {}
