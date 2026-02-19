package com.example.LegiTrack.service;

import com.example.LegiTrack.model.Bill;
import com.example.LegiTrack.model.BillSummaryEntity;
import com.example.LegiTrack.repository.BillSummaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class BillSummaryService {
    private BillSummaryRepository billSummaryRepository;
    private LegiScanService legiScanService;
    private BillTextService billTextService;

    @Autowired
    public BillSummaryService(BillSummaryRepository billSummaryRepository,
                              LegiScanService legiScanService,
                              BillTextService billTextService) {
        this.billSummaryRepository = billSummaryRepository;
        this.legiScanService = legiScanService;
        this.billTextService = billTextService;
    }

    public String getSummary(Long billId)  {
        log.info("Getting summary for bill {}", billId);

        Optional<BillSummaryEntity> cached = billSummaryRepository.findByBillId(billId);
        if (cached.isPresent()) {
            log.info("Returning cached summary for bill {}", billId);
            return cached.get().getSummaryText();
        }
         log.info("No cached summary found. Generating new summary for bill {}", billId);

        try {
            Bill billToSummarize = legiScanService.getBill(billId);
            Long mostRecentTextDocId = billTextService.getMostRecentTextDocId(billToSummarize.getTexts());
            String billText = billTextService.getBillText(mostRecentTextDocId);
            // Step 4: Generate summary (TODO - implement Claude API)
            String summaryText = generateSummary(billText); // Stub for now

            BillSummaryEntity billSummaryEntity = new BillSummaryEntity(
                billId,
                billToSummarize.getTitle(),
                mostRecentTextDocId,
                summaryText
            );
            billSummaryRepository.save(billSummaryEntity);

            log.info("Generated and cached summary for bill {}", billId);
            return summaryText;
        } catch (Exception e) {
            log.error("Error generating summary for bill {}: {}", billId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate summary for bill " + billId, e);
        }
    }

    /**
     * Placeholder for Claude API integration
     * TODO: Implement actual Claude API call
     */
    private String generateSummary(String billText) {
        // For now, return a placeholder
        return "Summary will be generated here using Claude AI. Bill text length: " + billText.length() + " characters.";
    }
}
