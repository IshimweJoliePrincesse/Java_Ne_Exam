package com.utility.billing.service.impl;

import com.utility.billing.exception.InvalidOperationException;
import com.utility.billing.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Override
    public void sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(message);
            mailSender.send(mail);
        } catch (MailException ex) {
            throw new InvalidOperationException("Email could not be sent to " + to + ". Check SMTP configuration and try again.");
        }
    }

    @Override
    public void sendOtpEmail(String to, String fullName, String otp) {
        sendEmail(to, "Verify your Utility Billing account",
                "Dear " + fullName + ",\n\nYour email verification OTP is " + otp + ". It expires in 15 minutes.\n\nUtility Billing System");
    }

    @Override
    public void sendRoleChangedEmail(String to, String fullName, String message) {
        sendEmail(to, "Utility Billing account role update",
                "Dear " + fullName + ",\n\n" + message + "\n\nIf you did not expect this change, please contact the administrator.\n\nUtility Billing System");
    }

    @Override
    public void sendPasswordResetOtpEmail(String to, String fullName, String otp) {
        sendEmail(to, "Utility Billing password reset",
                "Dear " + fullName + ",\n\nYour password reset OTP is " + otp + ". It expires in 15 minutes.\n\nUtility Billing System");
    }
}
