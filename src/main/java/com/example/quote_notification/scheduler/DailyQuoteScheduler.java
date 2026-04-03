package com.example.quote_notification.scheduler;

import com.example.quote_notification.entity.Subscriber;
import com.example.quote_notification.service.EmailService;
import com.example.quote_notification.service.QuoteService;
import com.example.quote_notification.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyQuoteScheduler {

    private final SubscriberService subscriberService;
    private final QuoteService quoteService;
    private final EmailService emailService;

    // Every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyQuotes() {
        log.info("Sending daily quotes...");
        Map<String, String> quote = quoteService.fetchDailyQuote();

        List<Subscriber> subscribers = subscriberService.getVerifiedSubscribers();
        for (Subscriber s : subscribers) {
            try {
                emailService.sendDailyQuote(
                        s.getEmail(), s.getName(),
                        quote.get("text"), quote.get("author")
                );
                log.info("Sent to {}", s.getEmail());
            } catch (Exception e) {
                log.error("Failed for {}: {}", s.getEmail(), e.getMessage());
            }
        }
    }
}
