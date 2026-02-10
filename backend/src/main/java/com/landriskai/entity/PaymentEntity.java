package com.landriskai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Payment entity - tracks payment state and reconciliation (FR-5, FR-6)
 * Supports multiple payment methods: UPI, Cards, Net Banking
 */
@Entity
@Table(name = "lr_payment", indexes = {
    @Index(name = "idx_payment_order", columnList = "order_id"),
    @Index(name = "idx_payment_ref", columnList = "payment_gateway_ref", unique = true),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_created", columnList = "created_at")
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the order
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private OrderEntity order;

    /**
     * Amount in paise (e.g., 2500 for Rs 25)
     */
    @Column(nullable = false)
    private Integer amountPaise;

    /**
     * Payment method: UPI, CARD, NETBANKING, WALLET
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    /**
     * Payment gateway name: RAZORPAY, PAYTM, CASHFREE, etc.
     */
    @Column(nullable = false, length = 50)
    private String paymentGateway;

    /**
     * Payment reference from gateway (transaction ID)
     */
    @Column(nullable = false, length = 100)
    private String paymentGatewayRef;

    /**
     * Status: INITIATED, PENDING, COMPLETED, FAILED, EXPIRED, CANCELLED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     * Webhook received and processed
     */
    @Column(nullable = false)
    private Boolean webhookReceived = false;

    /**
     * Last reconciliation attempt timestamp
     */
    private Instant lastReconciliationAt;

    /**
     * Reconciliation status: NOT_STARTED, IN_PROGRESS, MATCHED, MISMATCH
     */
    @Enumerated(EnumType.STRING)
    private ReconciliationStatus reconciliationStatus;

    /**
     * Payment failure reason (if applicable)
     */
    @Column(length = 500)
    private String failureReason;

    /**
     * Payment gateway error code (if applicable)
     */
    @Column(length = 100)
    private String gatewayErrorCode;

    /**
     * Refund reference (if refunded)
     */
    @Column(length = 100)
    private String refundRef;

    /**
     * Refund timestamp (if applicable)
     */
    private Instant refundedAt;

    /**
     * Payment confirmed/captured timestamp
     */
    private Instant confirmedAt;

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

    /**
     * Expires in 15 minutes if not confirmed (payment timeout)
     */
    private Instant expiresAt;

    public enum PaymentMethod {
        UPI,
        CARD,
        NETBANKING,
        WALLET
    }

    public enum PaymentStatus {
        INITIATED,      // Payment link generated
        PENDING,        // Awaiting payment
        COMPLETED,      // Successfully captured
        FAILED,         // Payment failed
        EXPIRED,        // Payment link expired
        CANCELLED,      // Payment cancelled by user
        REFUNDED        // Payment refunded
    }

    public enum ReconciliationStatus {
        NOT_STARTED,
        IN_PROGRESS,
        MATCHED,        // Reconciled successfully
        MISMATCH        // Amount/status mismatch
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        // Payment expires in 15 minutes
        this.expiresAt = Instant.now().plusSeconds(15 * 60);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
