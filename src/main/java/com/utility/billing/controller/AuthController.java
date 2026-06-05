package com.utility.billing.controller;

import com.utility.billing.dto.AuthDtos.*;
import com.utility.billing.dto.UserResponse;
import com.utility.billing.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "Register users, verify email OTP, login, and recover forgotten passwords.")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register customer account", description = "Creates an inactive customer user account and matching customer profile, then sends a 6-digit OTP to the registered email for activation.")
    public UserResponse register(@Valid @RequestBody UserRegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT", description = "Authenticates a verified active user and returns a Bearer JWT token for protected endpoints.")
    public JwtResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify email OTP", description = "Confirms the 6-digit OTP sent during registration and enables login for the account.")
    public Map<String, String> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        return authService.verifyOtp(request);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset OTP", description = "Generates a 6-digit password reset OTP and emails it to the registered user.")
    public Map<String, String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Changes the user's password using the OTP sent by email.")
    public Map<String, String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }
}
