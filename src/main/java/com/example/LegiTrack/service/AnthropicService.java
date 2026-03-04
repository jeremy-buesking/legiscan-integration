package com.example.LegiTrack.service;

import com.example.LegiTrack.config.AnthropicConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AnthropicService {
    private final RestTemplate restTemplate;
    private final AnthropicConfig config;
    private final ObjectMapper objectMapper;

    @Autowired
    public AnthropicService(RestTemplate restTemplate, AnthropicConfig config, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.objectMapper = objectMapper;
        log.info("=== AnthropicService initialized ===");
        log.info("Model: {}", config.getModel());
    }

    public String summarizeBill(String billText) {
        try {
            log.info("Generating summary for bill text ({} characters)", billText.length());

            AnthropicRequest request = buildAnthropicRequest(billText);
            log.info("Request details:");
            log.info("  Model: {}", request.getModel());
            log.info("  MaxTokens: {}", request.getMaxTokens());
            log.info("  Messages count: {}", request.getMessages().size());
            log.info("  URL: {}", config.getBaseUrl());

            HttpHeaders headers = buildHttpHeaders();
            log.info("Headers: {}", headers);

            // Try to serialize request to see the JSON
            try {
                String json = objectMapper.writeValueAsString(request);
                log.info("Request JSON: {}", json);
            } catch (Exception e) {
                log.warn("Could not serialize request for debugging");
            }

            // Combine into HTTP entity
            HttpEntity<AnthropicRequest> entity = new HttpEntity<>(request, headers);

            // Make API call
            log.info("Making POST request to Claude API...");
            ResponseEntity<AnthropicResponse> response = restTemplate.postForEntity(
                    config.getBaseUrl(),
                    entity,
                    AnthropicResponse.class
            );
            log.info("Received response with status: {}", response.getStatusCode());

            // Parse and return summary text
            String summary = parseResponse(response.getBody());
            log.info("Successfully generated summary ({} characters)", summary.length());
            return summary;

        }catch (RestClientException e) {
            log.error("Error calling Claude API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate summary", e);
        }
    }

    private AnthropicRequest buildAnthropicRequest(String billText) {
        AnthropicRequest anthropicRequest = new AnthropicRequest();
        anthropicRequest.setModel(config.getModel());
        anthropicRequest.setMaxTokens(config.getMaxTokens());

        List<AnthropicRequest.Message> messageList = new ArrayList<>();
        AnthropicRequest.Message message = new AnthropicRequest.Message();
        message.setRole("user");
        message.setContent(buildPrompt(billText));
        messageList.add(message);
        anthropicRequest.setMessages(messageList);

        return anthropicRequest;
    }

    private HttpHeaders buildHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("x-api-key", config.getApiKey());
        headers.add("anthropic-version", config.getApiVersion());
        return headers;
    }

    private String buildPrompt(String billText) {
        return String.format(
                "You are analyzing a legislative bill. Please provide a clear, " +
                        "factual summary in 2-3 paragraphs that covers:\n" +
                        "1. What the bill does\n" +
                        "2. Who it affects\n" +
                        "3. Key provisions or requirements\n\n" +
                        "Bill text:\n%s",
                billText
        );
    }

    private String parseResponse(AnthropicResponse response) {
        if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
            throw new RuntimeException("Empty response from Claude API");
        }

        // Get the first content block
        AnthropicResponse.Content content = response.getContent().get(0);

        if (content == null || content.getText() == null) {
            throw new RuntimeException("No text in Claude API response");
        }

        return content.getText();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class AnthropicRequest {
        private String model;

        @JsonProperty("max_tokens")
        private Integer maxTokens;

        private List<Message> messages;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Message {
            private String role;
            private String content;
        }
    }

    @Data
    @NoArgsConstructor
    private static class AnthropicResponse {
        private String id;
        private String type;
        private String role;
        private List<Content> content;

        @Data
        @NoArgsConstructor
        public static class Content {
            private String type;
            private String text;
        }
    }
}


