package com.example.quote_notification.controller;

import com.example.quote_notification.scheduler.DailyQuoteScheduler;
import com.example.quote_notification.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriberService service;

    @Autowired
    DailyQuoteScheduler scheduler;


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest req, Model model) {
        String result = service.register(req.email(), req.name());
        model.addAttribute("email", req.email());
        model.addAttribute("message", result.equals("ALREADY_VERIFIED")
                ? "You're already subscribed!" : "OTP sent to " + req.email());
        return "otp-verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String email,
                         @RequestParam String otp, Model model) {
        String result = service.verifyOtp(email, otp);
        model.addAttribute("success", result.equals("SUCCESS"));
        model.addAttribute("message", switch (result) {
            case "SUCCESS" -> "✅ Verified! You'll receive quotes every morning.";
            case "OTP_EXPIRED" -> "⏰ OTP expired. Please register again.";
            case "INVALID_OTP" -> "❌ Wrong OTP. Try again.";
            default -> result;
        });
        return "result";
    }
}

// Simple record/DTO
record RegisterRequest(String name, String email) {
    public RegisterRequest() { this(null, null); }
}
