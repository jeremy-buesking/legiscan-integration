package com.example.LegiTrack.service;

import com.example.LegiTrack.config.LegiScanConfig;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
public class BillTextService {
    private final RestTemplate restTemplate;
    private final LegiScanConfig config;

    @Autowired
    public BillTextService(RestTemplate restTemplate, LegiScanConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    /**
     * Fetches bill text from LegiScan API and extracts it based on MIME type
     *
     * @param docId The document ID from the bill's texts array
     * @return Extracted plain text from the bill
     * @throws RuntimeException if text extraction fails
     */
    public String getBillText(Long docId) {
        log.info("Getting bill text for docId {}", docId);

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(config.getBaseUrl())
                    .queryParam("key", config.getApiKey())
                    .queryParam("op", "getBillText")
                    .queryParam("id", docId)
                    .build()
                    .toUriString();

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            if (response == null || !response.has("text")) {
                throw new RuntimeException("response is null");
            }

            JsonNode textNode = response.get("text");
            String mimeType = textNode.get("mime").asText();
            String base64Doc = textNode.get("doc").asText();
            log.info("Retrieved document with mime type: {}", mimeType);

            // decode response text
            byte[] decodedBytes = Base64.getDecoder().decode(base64Doc);

            // check mime type
            if (mimeType.equals("application/pdf")) {
                return extractTextFromPdf(decodedBytes);
            } else if (mimeType.equals("text/html")) {
                return extractTextFromHtml(decodedBytes);
            } else {
                // assume plain text
                return new String(decodedBytes);
            }
        } catch(Exception e) {
            log.error("Error fetching bill text for doc_id {}: {}", docId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch bill text for doc_id " + docId, e);
        }
    }

    public String extractTextFromPdf(byte[] pdfBytes) throws IOException {
        log.debug("Extracting text from pdf ({} bytes)", pdfBytes.length);
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public String extractTextFromHtml(byte[] htmlBytes) throws IOException {
        log.debug("Extracting text from HTML ({} bytes)", htmlBytes.length);

        // For now, just convert to string
        // In the future, could use Jsoup to strip HTML tags properly
        String html = new String(htmlBytes);

        // Simple tag removal (not perfect, but works for basic HTML)
        String text = html.replaceAll("<[^>]+>", " ")
                .replaceAll("\\s+", " ")
                .trim();

        log.info("Successfully extracted {} characters from HTML", text.length());
        return text;
    }

    /**
     * Gets the most recent text version from a bill's texts array
     *
     * @param textsArray JsonNode array of text versions
     * @return The doc_id of the most recent version
     */
    public Long getMostRecentTextDocId(JsonNode textsArray) {
        if (textsArray == null || textsArray.size() == 0) {
            throw new RuntimeException("No text versions available for this bill");
        }

        // The most recent version is typically the last in the array
        JsonNode lastVersion = textsArray.get(textsArray.size() - 1);
        return lastVersion.get("doc_id").asLong();
    }
}
