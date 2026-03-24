package com.example.LegiTrack.controller;

import com.example.LegiTrack.model.domain.Bill;
import com.example.LegiTrack.service.BillSummaryService;
import com.example.LegiTrack.service.BillTextService;
import com.example.LegiTrack.service.LegiScanService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Controller to expose API endpoints
@RestController
@RequestMapping("/api/legiscan")
@CrossOrigin(origins = "http://localhost:3000")
public class LegiScanController {
    private final LegiScanService legiScanService;
    private final BillTextService billTextService;
    private final BillSummaryService billSummaryService;

    @Autowired
    public LegiScanController(
            LegiScanService legiScanService, BillTextService billTextService, BillSummaryService billSummaryService) {
        this.legiScanService = legiScanService;
        this.billTextService = billTextService;
        this.billSummaryService = billSummaryService;
    }

    @GetMapping("/masterList")
    public ResponseEntity<JsonNode> getMasterList(@RequestParam String state) {
        JsonNode response = legiScanService.getMasterList(state);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<Bill> getBill(@PathVariable Long billId) {
        Bill response = legiScanService.getBill(billId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bill/{billId}/summary")
    public ResponseEntity<Map<String, Object>> getBillSummary(@PathVariable Long billId) {
        try {
            String summary = billSummaryService.getSummary(billId);

            Map<String, Object> response = new HashMap<>();
            response.put("billId", billId);
            response.put("summary", summary);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to generate summary: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<JsonNode> searchBills(
            @RequestParam String query,
            @RequestParam String state) {
        JsonNode response = legiScanService.searchBills(query, state);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/text/{docId}")
    public ResponseEntity<String> getBillText(@PathVariable Long docId) {
        String response = billTextService.getBillText(docId);
        return ResponseEntity.ok(response);
    }
}
