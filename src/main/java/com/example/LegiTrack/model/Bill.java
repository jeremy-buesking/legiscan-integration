package com.example.LegiTrack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
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
    private String status;

    @JsonProperty("introduced_date")
    private String introducedDate;

    @JsonProperty("last_action_date")
    private String lastActionDate;

    @JsonProperty("last_action")
    private String lastAction;

    @JsonProperty("sponsors")
    private String sponsors;
}
