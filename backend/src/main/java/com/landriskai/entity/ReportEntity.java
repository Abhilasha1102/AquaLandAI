package com.landriskai.entity;

import com.landriskai.domain.RiskBand;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "lr_report")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One order -> one report in MVP
    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskBand riskBand;

    @Column(nullable = false)
    private Integer riskScore; // 0-100

    @Column(nullable = false)
    private String verificationCode; // for verify endpoint

    @Column(nullable = false)
    private String pdfPath; // local file path

    /**
     * Report delivery status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportDeliveryStatus deliveryStatus = ReportDeliveryStatus.PENDING;

    /**
     * PDF link expiration timestamp (7 days by default)
     */
    @Column(nullable = false)
    private Instant pdfExpiresAt;

    /**
     * Report rating 1-5 (FR-14: rating submission)
     */
    private Integer userRating;

    /**
     * User feedback for the report
     */
    @Column(columnDefinition = "TEXT", length = 1000)
    private String userFeedback;

    /**
     * Feedback submitted timestamp
     */
    private Instant feedbackSubmittedAt;

    /**
     * Whether report was regenerated
     */
    @Column(nullable = false)
    private Boolean isRegenerated = false;

    /**
     * Parent report ID (if this is a regenerated report)
     */
    private Long parentReportId;

    @Column(nullable = false, updatable = false)
    private Instant generatedAt;

    @Column(nullable = false)
    private String summaryJson; // compact, for verify page

    @PrePersist
    protected void onCreate() {
        this.generatedAt = Instant.now();
        // PDF links expire in 7 days
        this.pdfExpiresAt = Instant.now().plusSeconds(7 * 24 * 3600L);
    }

    public enum ReportDeliveryStatus {
        PENDING,        // Not yet delivered
        DELIVERED,      // Successfully delivered
        FAILED,         // Delivery failed (retry in progress)
        MANUAL_DOWNLOAD // User manually downloaded (no WhatsApp)
    }
}
