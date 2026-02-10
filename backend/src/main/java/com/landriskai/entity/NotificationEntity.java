package com.landriskai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Notification entity - tracks delivery of reports via WhatsApp, Email, SMS
 * Supports retry logic and delivery status tracking (FR-11, NFR-3)
 */
@Entity
@Table(name = "lr_notification", indexes = {
    @Index(name = "idx_notification_report", columnList = "report_id"),
    @Index(name = "idx_notification_status", columnList = "status"),
    @Index(name = "idx_notification_type", columnList = "notification_type"),
    @Index(name = "idx_notification_created", columnList = "created_at")
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the report
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;

    /**
     * Reference to the order (for quick lookup)
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * Notification type: WHATSAPP, EMAIL, SMS
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    /**
     * Recipient phone/email address (encrypted for sensitive data)
     */
    @Column(nullable = false, length = 512)
    private String recipientEncrypted;

    /**
     * Provider gateway: TWILIO, WHATSAPP_BUSINESS, SENDGRID, etc.
     */
    @Column(length = 50)
    private String provider;

    /**
     * Provider reference ID (for tracking)
     */
    @Column(length = 200)
    private String providerRef;

    /**
     * Delivery status: PENDING, SENT, DELIVERED, FAILED, BOUNCED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    /**
     * Retry count (incremented for each retry)
     */
    @Column(nullable = false)
    private Integer retryCount = 0;

    /**
     * Max retries allowed
     */
    @Column(nullable = false)
    private Integer maxRetries = 3;

    /**
     * Next retry timestamp
     */
    private Instant nextRetryAt;

    /**
     * Error message (if delivery failed)
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Gateway error code (if applicable)
     */
    @Column(length = 100)
    private String gatewayErrorCode;

    /**
     * Sent timestamp
     */
    private Instant sentAt;

    /**
     * Delivered timestamp (confirmed delivery)
     */
    private Instant deliveredAt;

    /**
     * Read/opened timestamp (if applicable)
     */
    private Instant readAt;

    /**
     * Created timestamp
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Last updated timestamp
     */
    @Column(nullable = false)
    private Instant updatedAt;

    public enum NotificationType {
        WHATSAPP,
        EMAIL,
        SMS
    }

    public enum DeliveryStatus {
        PENDING,        // Queued for delivery
        SENT,          // Sent to provider
        DELIVERED,     // Confirmed delivered
        FAILED,        // Delivery failed (will not retry)
        BOUNCED        // Invalid recipient
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
