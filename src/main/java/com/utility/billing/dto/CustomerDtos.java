package com.utility.billing.dto;

import com.utility.billing.enums.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerDtos {
    public record CustomerRequest(
            @NotBlank @Size(min = 2, max = 150) String fullName,
            @NotBlank @Pattern(regexp = "\\d{16}", message = "National ID must be exactly 16 digits") String nationalId,
            @Email @NotBlank @Size(max = 150) String email,
            @NotBlank @Pattern(regexp = AuthDtos.PHONE_REGEX, message = "Phone number must start with + and contain 10 to 12 digits") String phoneNumber,
            @NotBlank @Size(min = 3, max = 255) String address) {}
    public record CustomerProfileUpdateRequest(
            @NotBlank @Size(min = 2, max = 150) String fullName,
            @NotBlank @Pattern(regexp = AuthDtos.PHONE_REGEX, message = "Phone number must start with + and contain 10 to 12 digits") String phoneNumber,
            @NotBlank @Size(min = 3, max = 255) String address) {}
    public record StatusRequest(@NotNull Status status) {}
    public record CustomerResponse(UUID id, String fullName, String nationalId, String email, String phoneNumber, String address, Status status, LocalDateTime createdAt, LocalDateTime updatedAt) {}
}
