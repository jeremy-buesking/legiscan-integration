package com.example.LegiTrack.service;

import com.example.LegiTrack.config.LegiScanConfig;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LegiScan Service Unit Tests")
public class LegiScanServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private LegiScanConfig config;
    @InjectMocks
    private LegiScanService legiScanService;

    private final HashMap<String, JsonNode> billCache = new HashMap<>();

    @BeforeEach
    void setUp() {
        when(config.getBaseUrl()).thenReturn("https://api.legiscan.com");
        when(config.getApiKey()).thenReturn("test-api-key");
    }

    // ==================== getMasterList() Tests ====================



    // ==================== getBill() Tests ====================



    // ==================== searchBills() Tests ====================


}
