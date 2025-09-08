package com.example.backend.url;

import java.time.Instant;

public class ShortUrl {
    private String shortcode;
    private String originalUrl;
    private Instant expiry;
    private long hits;

    public ShortUrl(String shortcode, String originalUrl, Instant expiry) {
        this.shortcode = shortcode;
        this.originalUrl = originalUrl;
        this.expiry = expiry;
        this.hits = 0L;
    }

    public String getShortcode() { return shortcode; }
    public String getOriginalUrl() { return originalUrl; }
    public Instant getExpiry() { return expiry; }
    public long getHits() { return hits; }
    public void incrementHits() { this.hits++; }
}


