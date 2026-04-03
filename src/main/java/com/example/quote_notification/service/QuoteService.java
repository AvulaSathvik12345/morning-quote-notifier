package com.example.quote_notification.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class QuoteService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> fetchDailyQuote() {
        String url = "https://zenquotes.io/api/random";
        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {});

        Map<String, Object> quote = Objects.requireNonNull(response.getBody()).get(0);
        return Map.of(
                "text", (String) quote.get("q"),
                "author", (String) quote.get("a")
        );
    }
}