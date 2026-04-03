package com.example.quote_notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendOtpEmail(String toEmail, String name, String otp) {
        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("otp", otp);

        String html = templateEngine.process("otp-email", ctx);
        sendHtmlMail(toEmail, "Verify your subscription", html);
    }

    public void sendDailyQuote(String toEmail, String name,
                               String quoteText, String quoteAuthor) {
        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("quote", quoteText);
        ctx.setVariable("author", quoteAuthor);
        ctx.setVariable("date", LocalDate.now().format(
                DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

        String html = templateEngine.process("daily-quote-email", ctx);
        sendHtmlMail(toEmail, "☀️ Your Morning Quote", html);
    }

    private void sendHtmlMail(String to, String subject, String html) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}