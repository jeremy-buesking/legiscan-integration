package com.example.LegiTrack.controller;

import com.example.LegiTrack.service.LegiScanService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller to expose API endpoints
@RestController
@RequestMapping("/api/legiscan")
@CrossOrigin(origins = "http://localhost:3000")
public class LegiScanController {
    private final LegiScanService legiScanService;

    @Autowired
    public LegiScanController(LegiScanService legiScanService) {
        this.legiScanService = legiScanService;
    }

    @GetMapping("/masterList")
    public ResponseEntity<JsonNode> getMasterList(@RequestParam String state) {
        JsonNode response = legiScanService.getMasterList(state);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<JsonNode> getBill(@PathVariable String billId) {
        JsonNode response = legiScanService.getBill(billId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<JsonNode> searchBills(
            @RequestParam String query,
            @RequestParam String state) {
        JsonNode response = legiScanService.searchBills(query, state);
        return ResponseEntity.ok(response);
    }
}
