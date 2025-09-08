package com.example.backend.url;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlService {
    private final Map<String, ShortUrl> store = new ConcurrentHashMap<>();
    private final LoggingClient loggingClient;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();

    public UrlService(LoggingClient loggingClient) {
        this.loggingClient = loggingClient;
    }

    public ShortUrl create(String url, Integer validityMinutes, String shortcode) {
        int validity = validityMinutes != null ? validityMinutes : 30;
        Instant expiry = Instant.now().plus(validity, ChronoUnit.MINUTES);
        String code = (shortcode != null && !shortcode.isBlank()) ? shortcode : generateCode(6);
        if (store.containsKey(code)) {
            code = generateCode(7);
        }
        ShortUrl shortUrl = new ShortUrl(code, url, expiry);
        store.put(code, shortUrl);
        loggingClient.log("backend", "info", "handler", "created shortcode: " + code);
        return shortUrl;
    }

    public ShortUrl get(String code) { return store.get(code); }

    public void hit(String code) {
        ShortUrl s = store.get(code);
        if (s != null) s.incrementHits();
    }

    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}


