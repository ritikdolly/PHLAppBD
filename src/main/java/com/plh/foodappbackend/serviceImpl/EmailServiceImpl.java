package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendWelcomeEmail(String to) {
        sendEmail(
                to,
                "Welcome to Food App",
                buildWelcomeEmailBody());
    }

    @Value("${website_name:Food App}")
    private String websiteName;

    private String buildWelcomeEmailBody() {
        return """
                Dear Customer,

                Welcome to %s !

                We’re delighted to have you on board. With Food App, you can explore a wide range of delicious meals, place orders effortlessly, and enjoy fast and reliable delivery.

                If you need any assistance, our support team is always here to help.

                Warm regards,
                %s Team
                """
                .formatted(websiteName, websiteName);
    }

    @Override
    public void sendLoginAlert(String to) {
        sendEmail(
                to,
                "Login Alert – Successful Sign-In",
                buildLoginAlertEmailBody());
    }

    private String buildLoginAlertEmailBody() {
        return """
                Dear Customer,

                This is to inform you that your account was successfully logged in.

                If this wasn’t you, please secure your account immediately by changing your password.

                Best regards,
                Food App Security Team
                """;
    }

    // @Override
    // public void sendVerificationOtp(String to, String otp) {
    // sendEmail(to, "Verification OTP", "Your verification OTP is: " + otp);
    // }

    @Override
    public void sendVerificationOtp(String to, String otp) {
        String subject = "Your Email Verification OTP";
        String body = buildOtpEmailBody(otp);
        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    private String buildOtpEmailBody(String otp) {
        return """
                Dear Customer,

                Thank you for initiating the email verification process.

                Your One-Time Password (OTP) is:

                %s

                This OTP is valid for a limited time. Please do not share it with anyone.

                If you did not request this, please ignore this email.

                Best regards,
                Support Team
                """.formatted(otp);
    }
}
