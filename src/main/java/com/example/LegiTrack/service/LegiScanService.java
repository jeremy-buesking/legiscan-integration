package com.example.LegiTrack.service;

import com.example.LegiTrack.config.LegiScanConfig;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

// Service class to handle LegiScan API calls w/ business logic
@Service
public class LegiScanService {
    private final RestTemplate restTemplate;
    private final LegiScanConfig config;
    private static final Logger logger = LoggerFactory.getLogger(LegiScanService.class);
    private final HashMap<String, JsonNode> billCache = new HashMap<>();

    @Autowired
    public LegiScanService(RestTemplate restTemplate, LegiScanConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public JsonNode getMasterList(String state) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(config.getBaseUrl())
                    .queryParam("key", config.getApiKey())
                    .queryParam("op", "getMasterList")
                    .queryParam("state", state)
                    .build()
                    .toUriString();

            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            return response.getBody();
        } catch (RestClientException e) {
            logger.error("Error fetching master data from LegiScan", e);
            throw new RuntimeException("Failed to fetch bill data", e);
        }
    }

    public JsonNode getBill(String billId) {
        try {
            if(billCache.containsKey(billId)) {
                return billCache.get(billId);
            }
            String url = UriComponentsBuilder
                    .fromHttpUrl(config.getBaseUrl())
                    .queryParam("key", config.getApiKey())
                    .queryParam("op", "getBill")
                    .queryParam("id", billId)
                    .build()
                    .toUriString();

            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            billCache.put(billId, response.getBody());
            return response.getBody();
        } catch (RestClientException e) {
            logger.error("Error fetching bill data from LegiScan", e);
            throw new RuntimeException("Failed to fetch bill data", e);
        }
    }

    public JsonNode searchBills(String query, String state) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(config.getBaseUrl())
                    .queryParam("key", config.getApiKey())
                    .queryParam("op", "search")
                    .queryParam("state", state)
                    .queryParam("query", query)
                    .build()
                    .toUriString();

            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            return response.getBody();
        } catch (RestClientException e) {
            logger.error("Error searching bills in LegiScan", e);
            throw new RuntimeException("Failed to search bills", e);
        }
    }
}