package com.example.backend.url;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UrlController {
    private final UrlService service;
    private final LoggingClient logger;

    public UrlController(UrlService service, LoggingClient logger) {
        this.service = service;
        this.logger = logger;
    }

    @PostMapping("/shorturls")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        String url = (String) body.get("url");
        Integer validity = body.get("validity") instanceof Number ? ((Number) body.get("validity")).intValue() : null;
        String shortcode = (String) body.get("shortcode");

        Assert.hasText(url, "url is required");

        ShortUrl created = service.create(url, validity, shortcode);
        Map<String, Object> resp = new HashMap<>();
        resp.put("shortLink", "/" + created.getShortcode());
        resp.put("expiry", created.getExpiry().toString());
        logger.log("backend", "info", "handler", "returning shortLink");
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        ShortUrl s = service.get(code);
        if (s == null || s.getExpiry().isBefore(Instant.now())) {
            logger.log("backend", "warn", "handler", "invalid or expired code: " + code);
            return ResponseEntity.notFound().build();
        }
        service.hit(code);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(s.getOriginalUrl()));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/shorturls/{code}/stats")
    public ResponseEntity<Map<String, Object>> stats(@PathVariable String code) {
        ShortUrl s = service.get(code);
        if (s == null) return ResponseEntity.notFound().build();
        Map<String, Object> resp = new HashMap<>();
        resp.put("shortcode", s.getShortcode());
        resp.put("hits", s.getHits());
        resp.put("expiry", s.getExpiry().toString());
        return ResponseEntity.ok(resp);
    }
}


