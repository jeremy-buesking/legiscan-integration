package com.example.LegiTrack.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bill_summaries")
public class BillSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_id", nullable = false, unique = true)
    private Long billId;

    @Column(name = "bill_title")
    private String billTitle;

    @Column(name = "doc_id")
    private Long docId;

    @Column(name = "summary_text", columnDefinition = "TEXT")
    private String summaryText;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public BillSummaryEntity(Long billId, String billTitle, Long docId, String summaryText) {
        this.billId = billId;
        this.billTitle = billTitle;
        this.docId = docId;
        this.summaryText = summaryText;
        this.createdDate = LocalDateTime.now();
    }
}
