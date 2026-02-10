package com.landriskai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * User entity - represents customers and resellers
 * Phone numbers are encrypted at rest for security compliance (NFR-5)
 */
@Entity
@Table(name = "lr_user", indexes = {
    @Index(name = "idx_user_phone_hash", columnList = "phone_hash"),
    @Index(name = "idx_user_created", columnList = "created_at")
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Encrypted phone number for WhatsApp delivery
     * Store encrypted value, use phone_hash for lookups
     */
    @Column(nullable = false, length = 512)
    private String phoneEncrypted;

    /**
     * Hash of phone for secure lookups without decryption
     * SHA-256 hash of the actual phone number
     */
    @Column(nullable = false, length = 64, unique = true)
    private String phoneHash;

    /**
     * Optional email for report delivery and communications
     */
    @Column(length = 255)
    private String email;

    /**
     * User full name or business name
     */
    @Column(length = 255)
    private String displayName;

    /**
     * User type: CUSTOMER, RESELLER, SUPPORT_AGENT, ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    /**
     * Whether WhatsApp consent given (NFR-8: clear consent language)
     */
    @Column(nullable = false)
    private Boolean whatsappConsent = false;

    /**
     * Whether email consent given
     */
    @Column(nullable = false)
    private Boolean emailConsent = false;

    /**
     * User account status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Total reports generated (for analytics)
     */
    @Column(nullable = false)
    private Integer totalReports = 0;

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
     * Last login timestamp
     */
    private Instant lastLoginAt;

    /**
     * Data retention policy expiry (after which phone/personal data auto-deleted)
     */
    @Column(nullable = false)
    private Instant dataRetentionUntil;

    public enum UserType {
        CUSTOMER,           // Individual end-user
        RESELLER,          // Reseller/Agent
        SUPPORT_AGENT,     // Support staff
        ADMIN              // System admin
    }

    public enum UserStatus {
        ACTIVE,
        SUSPENDED,
        DELETED
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        // Default retention: 90 days from now
        this.dataRetentionUntil = Instant.now().plusSeconds(90 * 24 * 3600L);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
