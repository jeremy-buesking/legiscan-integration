package com.example.LegiTrack.controller;

import com.example.LegiTrack.model.Bill;
import com.example.LegiTrack.service.BillTextService;
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
    private final BillTextService billTextService;

    @Autowired
    public LegiScanController(LegiScanService legiScanService, BillTextService billTextService) {
        this.legiScanService = legiScanService;
        this.billTextService = billTextService;
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
