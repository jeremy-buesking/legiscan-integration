package com.example.LegiTrack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Complete Bill representation with all details
 * Used when fetching individual bill information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    @JsonProperty("bill_id")
    private Long billId;

    @JsonProperty("state")
    private String state;

    @JsonProperty("bill_number")
    private String billNumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private Integer status;  // Numeric status code

    @JsonProperty("status_date")
    private String statusDate;

    @JsonProperty("url")
    private String url;

    @JsonProperty("state_link")
    private String stateLink;

    @JsonProperty("sponsors")
    private List<Sponsor> sponsors;

    @JsonProperty("history")
    private List<HistoryItem> history;

    @JsonProperty("texts")
    private List<BillText> texts;

    // Nested classes for complex types

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sponsor {
        @JsonProperty("people_id")
        private Long peopleId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("party")
        private String party;

        @JsonProperty("role")
        private String role;  // "Primary" or "Cosponsor"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryItem {
        @JsonProperty("date")
        private String date;

        @JsonProperty("action")
        private String action;

        @JsonProperty("chamber")
        private String chamber;

        @JsonProperty("importance")
        private Integer importance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillText {
        @JsonProperty("doc_id")
        private Long docId;

        @JsonProperty("date")
        private String date;

        @JsonProperty("type")
        private String type;  // "Introduced", "Amended", "Enrolled", etc.

        @JsonProperty("mime")
        private String mime;  // "application/pdf" or "text/html"

        @JsonProperty("url")
        private String url;
    }
}
