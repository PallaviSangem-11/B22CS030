package com.example.backend.url;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingClient {

    @Value("${log.endpoint:http://20.244.56.144/evaluation-service/logs}")
    private String logEndpoint;

    @Value("${log.token:}")
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    public void log(String stack, String level, String pkg, String message) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("stack", stack.toLowerCase());
            body.put("level", level.toLowerCase());
            body.put("package", pkg.toLowerCase());
            body.put("message", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null && !token.isBlank()) {
                headers.set("Authorization", "Bearer " + token);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(logEndpoint, entity, String.class);
        } catch (RestClientException ignored) {
        }
    }
}


