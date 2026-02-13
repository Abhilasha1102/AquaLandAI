package com.landriskai.entity;

import com.landriskai.domain.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "lr_order",
    indexes = {
        @Index(name = "idx_order_whatsapp", columnList = "whatsapp_number"),
        @Index(name = "idx_order_payment_ref", columnList = "payment_ref")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_order_payment_ref", columnNames = "payment_ref")
    }
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Location + parcel identifiers
    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String circle;

    @Column(nullable = false)
    private String village;

    @Column(nullable = false)
    private String khata;

    @Column(nullable = false)
    private String khesra;

    // Optional fields to improve matching (MVP)
    private String ownerName;
    private String plotArea;

    // Delivery
    @Column(nullable = false)
    private String whatsappNumber;

    // Payment
    @Column(nullable = false)
    private Integer amountPaise; // 2500 for Rs 25
    @Column(length = 100, unique = true)
    private String paymentRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * Optional email for delivery fallback
     */
    @Column(length = 255)
    private String emailAddress;

    /**
     * Reseller reference (if order placed through reseller)
     */
    private Long resellerId;

    /**
     * Delivery attempt count
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer deliveryAttempts = 0;

    /**
     * Whether delivery was successful
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean deliverySuccessful = false;

    /**
     * Payment expiry (order expires after 15 minutes if not paid)
     */
    private Instant paymentExpiresAt;

    @Column(nullable = false, updatable = false)
    private Instant createTime;

    @Column(nullable = false)
    private Instant updateTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = Instant.now();
        this.updateTime = Instant.now();
        // Payment expires in 15 minutes
        this.paymentExpiresAt = Instant.now().plusSeconds(15 * 60);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = Instant.now();
    }
}
