package com.utility.billing.dto;

import com.utility.billing.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public static final String PHONE_REGEX = "^\\+\\d{10,12}$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";

    public record UserRegisterRequest(
            @NotBlank @Size(min = 2, max = 150) String fullName,
            @Email @NotBlank @Size(max = 150) String email,
            @NotBlank @Pattern(regexp = PHONE_REGEX, message = "Phone number must start with + and contain 10 to 12 digits") String phoneNumber,
            @NotBlank @Pattern(regexp = "\\d{16}", message = "National ID must be exactly 16 digits") String nationalId,
            @NotBlank @Size(min = 3, max = 255) String address,
            @NotBlank @Pattern(regexp = PASSWORD_REGEX, message = "Password must have uppercase, lowercase, number, special character, and at least 8 characters") String password) {}

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record JwtResponse(String token, String tokenType, UserResponse user) {}
    public record ForgotPasswordRequest(@Email @NotBlank String email) {}
    public record ResetPasswordRequest(
            @Email @NotBlank String email,
            @NotBlank @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits") String otp,
            @NotBlank @Pattern(regexp = PASSWORD_REGEX, message = "Password must have uppercase, lowercase, number, special character, and at least 8 characters") String newPassword) {}
    public record OtpVerificationRequest(@Email @NotBlank String email, @NotBlank @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits") String otp) {}
    public record RoleUpdateRequest(@NotNull Role role) {}
}
