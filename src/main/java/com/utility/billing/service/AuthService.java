package com.utility.billing.service;

import com.utility.billing.dto.AuthDtos.*;
import com.utility.billing.dto.UserResponse;

import java.util.Map;

public interface AuthService {
    UserResponse register(UserRegisterRequest request);
    JwtResponse login(LoginRequest request);
    Map<String, String> verifyOtp(OtpVerificationRequest request);
    Map<String, String> forgotPassword(ForgotPasswordRequest request);
    Map<String, String> resetPassword(ResetPasswordRequest request);
}
