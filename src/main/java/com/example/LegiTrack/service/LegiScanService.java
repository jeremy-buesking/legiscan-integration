package com.example.LegiTrack.service;

import com.example.LegiTrack.config.LegiScanConfig;
import com.example.LegiTrack.model.domain.Bill;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

// Service class to handle LegiScan API calls w/ business logic
@Service
@Slf4j
public class LegiScanService {
    private final RestTemplate restTemplate;
    private final LegiScanConfig config;
    private final HashMap<Long, Bill> billCache = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public LegiScanService(RestTemplate restTemplate, LegiScanConfig config, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.objectMapper = objectMapper;
    }

    public JsonNode getMasterList(String state) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(config.getBaseUrl())
                    .queryParam("key", config.getApiKey())
                    .queryParam("op", "getMasterList")
                    .queryParam("state", state)
                    .build()
                    .toUriString();

            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error fetching master data from LegiScan", e);
            throw new RuntimeException("Failed to fetch bill data", e);
        }
    }

    public Bill getBill(Long billId) {
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
            JsonNode body = response.getBody();
            Bill billObj = objectMapper.treeToValue(body.get("bill"), Bill.class);
            billCache.put(billId, billObj);

            return billObj;
        } catch (RestClientException e) {
            log.error("Error fetching bill data from LegiScan", e);
            throw new RuntimeException("Failed to fetch bill data", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse bill data", e);
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
            log.error("Error searching bills in LegiScan", e);
            throw new RuntimeException("Failed to search bills", e);
        }
    }
}