package com.example.quote_notification.service;

import com.example.quote_notification.entity.Subscriber;
import com.example.quote_notification.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriberService {

    private final SubscriberRepository repo;
    private final EmailService emailService;

    @Value("${app.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    public String register(String email, String name) {
        Subscriber sub = repo.findByEmail(email)
                .orElse(new Subscriber());

        if (sub.isVerified()) return "ALREADY_VERIFIED";

        sub.setEmail(email);
        sub.setName(name);
        String otp = generateOtp();
        sub.setOtp(otp);
        sub.setOtpExpiry(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        repo.save(sub);

        emailService.sendOtpEmail(email, name, otp);
        return "OTP_SENT";
    }

    public String verifyOtp(String email, String otp) {
        Subscriber sub = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (sub.isVerified()) return "ALREADY_VERIFIED";
        if (LocalDateTime.now().isAfter(sub.getOtpExpiry())) return "OTP_EXPIRED";
        if (!sub.getOtp().equals(otp)) return "INVALID_OTP";

        sub.setVerified(true);
        sub.setOtp(null);
        sub.setOtpExpiry(null);
        repo.save(sub);
        return "SUCCESS";
    }

    public List<Subscriber> getVerifiedSubscribers() {
        return repo.findAllByVerifiedTrue();
    }

    private String generateOtp() {
        return String.format("%06d", new SecureRandom().nextInt(999999));
    }
}