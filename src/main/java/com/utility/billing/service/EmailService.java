package com.utility.billing.service;

public interface EmailService {
    void sendEmail(String to, String subject, String message);
    void sendOtpEmail(String to, String fullName, String otp);
    void sendRoleChangedEmail(String to, String fullName, String message);
    void sendPasswordResetOtpEmail(String to, String fullName, String otp);
}
