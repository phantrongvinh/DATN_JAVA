package com.datn.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ModerationService {
    private final WebClient webClient;

    public ModerationService(@Value("${openai.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public ModerationResult check(String text) {
        if (text == null || text.isBlank()) {
            return new ModerationResult(false, null);
        }
        try {
            Map<String, Object> response = webClient.post()
                    .uri("/moderations")
                    .bodyValue(Map.of("input", text, "model", "omni-moderation-latest"))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            Map<String, Object> result = results.get(0);
            boolean flagged = (boolean) result.get("flagged");

            String reason = null;
            if (flagged) {
                Map<String, Boolean> categories = (Map<String, Boolean>) result.get("categories");
                reason = categories.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse("vi phạm nội dung");
            }
            return new ModerationResult(flagged, reason);
        } catch (Exception e) {
            // Fail-open: API lỗi/timeout thì KHÔNG chặn, tránh downtime OpenAI ảnh hưởng cả site
            return new ModerationResult(false, null);
        }
    }

    public record ModerationResult(boolean flagged, String reason) {}
}
