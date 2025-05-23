package com.example.LegiTrack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// Configuration class to hold API properties
@Configuration
@ConfigurationProperties(prefix = "legiscan")
public class LegiScanConfig {
    private String apiKey;
    private String baseUrl = "https://api.legiscan.com";

    // Getters and setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
}
