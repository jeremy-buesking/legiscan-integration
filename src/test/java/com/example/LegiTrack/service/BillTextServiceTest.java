package com.example.LegiTrack.service;

import com.example.LegiTrack.config.LegiScanConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BillTextService
 *
 * Testing Strategy:
 * - Mock external dependencies (RestTemplate, LegiScanConfig)
 * - Test each method in isolation
 * - Cover success cases, edge cases, and error scenarios
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BillTextService Unit Tests")
class BillTextServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private LegiScanConfig config;
    @InjectMocks
    private BillTextService billTextService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Configure mock config to return test values
        when(config.getBaseUrl()).thenReturn("https://api.legiscan.com");
        when(config.getApiKey()).thenReturn("test-api-key");
    }

    // ========== getBillText() Tests ==========

    @Test
    @DisplayName("Should successfully extract text from PDF")
    void getBillText_WithPdfMimeType_ExtractsTextSuccessfully() throws Exception {
        // Arrange
        Long docId = 123456L;
        String mockPdfContent = "pdf test bill text";
        String base64Pdf = Base64.getEncoder().encodeToString(createSimplePdf(mockPdfContent));

        JsonNode mockResponse = createMockApiResponse(docId, "application/pdf", base64Pdf);

        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                .thenReturn(mockResponse);

        // Act
        String result = billTextService.getBillText(docId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).contains(mockPdfContent);

        // Verify
        verify(restTemplate, times(1)).getForObject(anyString(), eq(JsonNode.class));
    }

    @Test
    @DisplayName("Should successfully extract text from HTML")
    void getBillText_WithHtmlMimeType_ExtractsTextSuccessfully() throws Exception {
        // Arrange
        Long docId = 123456L;
        String htmlContent = "<html><body><h1>Bill Title</h1><p>Bill content here</p></body></html>";
        String base64Html = Base64.getEncoder().encodeToString(htmlContent.getBytes());

        JsonNode mockResponse = createMockApiResponse(docId, "text/html", base64Html);

        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                .thenReturn(mockResponse);

        // Act
        String result = billTextService.getBillText(docId);

        // Assert
        assertThat(result)
                .isNotNull()
                .contains("Bill Title")
                .contains("Bill content here")
                .doesNotContain("<html>")  // HTML tags should be stripped
                .doesNotContain("<p>");
    }

    @Test
    @DisplayName("Should handle plain text mime type")
    void getBillText_WithPlainTextMimeType_ReturnsTextDirectly() throws Exception {
        // Arrange
        Long docId = 123456L;
        String plainText = "Simple plain text bill content";
        String base64Text = Base64.getEncoder().encodeToString(plainText.getBytes());

        JsonNode mockResponse = createMockApiResponse(docId, "text/plain", base64Text);

        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                .thenReturn(mockResponse);

        // Act
        String result = billTextService.getBillText(docId);

        // Assert
        assertThat(result).isEqualTo(plainText);
    }

    @Test
    @DisplayName("Should throw exception when API returns null")
    void getBillText_WhenApiReturnsNull_ThrowsException() {
        // Arrange
        Long docId = 123456L;
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                .thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> billTextService.getBillText(docId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid response");
    }

    @Test
    @DisplayName("Should throw exception when API returns malformed response")
    void getBillText_WhenApiReturnsMalformedResponse_ThrowsException() throws Exception {
        // Arrange
        Long docId = 123456L;
        String malformedJson = "{\"status\": \"OK\"}";  // Missing "text" field
        JsonNode mockResponse = objectMapper.readTree(malformedJson);

        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        assertThatThrownBy(() -> billTextService.getBillText(docId))
                .isInstanceOf(RuntimeException.class);
    }

    // ========== extractTextFromHtml() Tests ==========

    @Test
    @DisplayName("Should strip HTML tags from content")
    void extractTextFromHtml_RemovesHtmlTags() throws Exception {
        // Arrange
        String html = "<div><h1>Title</h1><p>Paragraph 1</p><p>Paragraph 2</p></div>";
        byte[] htmlBytes = html.getBytes();

        // Act
        String result = billTextService.extractTextFromHtml(htmlBytes);

        // Assert
        assertThat(result)
                .doesNotContain("<div>")
                .doesNotContain("<h1>")
                .doesNotContain("<p>")
                .contains("Title")
                .contains("Paragraph 1")
                .contains("Paragraph 2");
    }

    @Test
    @DisplayName("Should handle HTML with nested tags")
    void extractTextFromHtml_HandlesNestedTags() throws Exception {
        // Arrange
        String html = "<div><section><article><p>Deeply nested content</p></article></section></div>";
        byte[] htmlBytes = html.getBytes();

        // Act
        String result = billTextService.extractTextFromHtml(htmlBytes);

        // Assert
        assertThat(result).contains("Deeply nested content");
    }

    @Test
    @DisplayName("Should normalize whitespace")
    void extractTextFromHtml_NormalizesWhitespace() throws Exception {
        // Arrange
        String html = "<p>Text   with    lots     of      spaces</p>";
        byte[] htmlBytes = html.getBytes();

        // Act
        String result = billTextService.extractTextFromHtml(htmlBytes);

        // Assert
        assertThat(result).isEqualTo("Text with lots of spaces");
    }

    // ========== getMostRecentTextDocId() Tests ==========

    @Test
    @DisplayName("Should return doc_id of last element in array")
    void getMostRecentTextDocId_ReturnsLastDocId() throws Exception {
        // Arrange
        String textsJson = """
            [
                {"doc_id": 100, "date": "2025-01-01"},
                {"doc_id": 200, "date": "2025-02-01"},
                {"doc_id": 300, "date": "2025-03-01"}
            ]
            """;
        JsonNode textsArray = objectMapper.readTree(textsJson);

        // Act
        Long result = billTextService.getMostRecentTextDocId(textsArray);

        // Assert
        assertThat(result).isEqualTo(300L);
    }

    @Test
    @DisplayName("Should handle single element array")
    void getMostRecentTextDocId_WithSingleElement_ReturnsDocId() throws Exception {
        // Arrange
        String textsJson = """
            [
                {"doc_id": 100, "date": "2025-01-01"}
            ]
            """;
        JsonNode textsArray = objectMapper.readTree(textsJson);

        // Act
        Long result = billTextService.getMostRecentTextDocId(textsArray);

        // Assert
        assertThat(result).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should throw exception for null array")
    void getMostRecentTextDocId_WithNullArray_ThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> billTextService.getMostRecentTextDocId(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No text versions available");
    }

    @Test
    @DisplayName("Should throw exception for empty array")
    void getMostRecentTextDocId_WithEmptyArray_ThrowsException() throws Exception {
        // Arrange
        JsonNode emptyArray = objectMapper.readTree("[]");

        // Act & Assert
        assertThatThrownBy(() -> billTextService.getMostRecentTextDocId(emptyArray))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No text versions available");
    }

    // ========== Helper Methods ==========

    /**
     * Creates a mock API response matching LegiScan's structure
     */
    private JsonNode createMockApiResponse(Long docId, String mimeType, String base64Doc) throws Exception {
        String responseJson = String.format("""
            {
                "status": "OK",
                "text": {
                    "doc_id": %d,
                    "bill_id": 999999,
                    "mime": "%s",
                    "doc": "%s"
                }
            }
            """, docId, mimeType, base64Doc);

        return objectMapper.readTree(responseJson);
    }

    /**
     * Creates a simple PDF byte array for testing
     * Note: This is a minimal PDF that PDFBox can parse
     */
    private byte[] createSimplePdf(String content) throws IOException {
        // For real testing, you'd create an actual PDF with PDFBox
        // For now, we'll create a minimal PDF structure
        // In practice, you might use a test PDF file from resources
        String pdfContent = "%PDF-1.4\n" +
                "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n" +
                "2 0 obj<</Type/Pages/Count 1/Kids[3 0 R]>>endobj\n" +
                "3 0 obj<</Type/Page/Parent 2 0 R/Contents 4 0 R>>endobj\n" +
                "4 0 obj<</Length " + content.length() + ">>stream\n" +
                content + "\nendstream\nendobj\n" +
                "xref\n0 5\ntrailer<</Size 5/Root 1 0 R>>\n%%EOF";

        return pdfContent.getBytes();
    }
}
