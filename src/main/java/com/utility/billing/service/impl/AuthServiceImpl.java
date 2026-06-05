package com.utility.billing.service.impl;

import com.utility.billing.dto.AuthDtos.*;
import com.utility.billing.dto.UserResponse;
import com.utility.billing.entity.AppUser;
import com.utility.billing.entity.Customer;
import com.utility.billing.enums.Role;
import com.utility.billing.enums.Status;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.InvalidOperationException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.UserMapper;
import com.utility.billing.repository.AppUserRepository;
import com.utility.billing.repository.CustomerRepository;
import com.utility.billing.service.AuthService;
import com.utility.billing.service.EmailService;
import com.utility.billing.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final CustomerRepository customerRepository;

    @Override
    public UserResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already registered");
        }
        if (customerRepository.existsByNationalId(request.nationalId())) {
            throw new DuplicateResourceException("Customer national ID already exists");
        }
        customerRepository.findByEmail(request.email()).ifPresent(customer -> {
            throw new DuplicateResourceException("A customer profile already exists for this email");
        });
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        AppUser user = AppUser.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .status(Status.INACTIVE)
                .role(Role.ROLE_CUSTOMER)
                .emailVerified(false)
                .otpCode(otp)
                .otpExpiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        AppUser saved = userRepository.save(user);
        Customer customer = Customer.builder()
                .fullName(request.fullName())
                .nationalId(request.nationalId())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .status(Status.INACTIVE)
                .build();
        customerRepository.save(customer);
        emailService.sendOtpEmail(saved.getEmail(), saved.getFullName(), otp);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        AppUser user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.isEmailVerified()) {
            throw new InvalidOperationException("Verify your email OTP before login");
        }
        return new JwtResponse(jwtUtil.generateToken(user), "Bearer", userMapper.toResponse(user));
    }

    @Override
    public Map<String, String> verifyOtp(OtpVerificationRequest request) {
        AppUser user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getOtpCode() == null || user.getOtpExpiresAt().isBefore(LocalDateTime.now()) || !user.getOtpCode().equals(request.otp())) {
            throw new InvalidOperationException("Invalid or expired OTP");
        }
        user.setEmailVerified(true);
        user.setStatus(Status.ACTIVE);
        user.setOtpCode(null);
        user.setOtpExpiresAt(null);
        customerRepository.findByEmail(user.getEmail()).ifPresent(customer -> customer.setStatus(Status.ACTIVE));
        return Map.of("message", "Email verified successfully");
    }

    @Override
    public Map<String, String> forgotPassword(ForgotPasswordRequest request) {
        AppUser user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        user.setPasswordResetToken(otp);
        user.setPasswordResetExpiresAt(LocalDateTime.now().plusMinutes(15));
        emailService.sendPasswordResetOtpEmail(user.getEmail(), user.getFullName(), otp);
        return Map.of("message", "Password reset OTP has been sent to your email");
    }

    @Override
    public Map<String, String> resetPassword(ResetPasswordRequest request) {
        AppUser user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getPasswordResetToken() == null
                || user.getPasswordResetExpiresAt() == null
                || user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())
                || !user.getPasswordResetToken().equals(request.otp())) {
            throw new InvalidOperationException("Invalid or expired password reset OTP");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        return Map.of("message", "Password reset successfully");
    }
}
