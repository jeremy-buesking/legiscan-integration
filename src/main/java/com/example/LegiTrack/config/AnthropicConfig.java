package com.example.LegiTrack.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "anthropic")
@Getter @Setter
public class AnthropicConfig {
    private String apiKey;
    private String baseUrl = "https://api.anthropic.com/v1/messages";
    private String model = "claude-3-5-sonnet-20240620";
    private Integer maxTokens = 1024;
    private String apiVersion = "2023-06-01";
}
