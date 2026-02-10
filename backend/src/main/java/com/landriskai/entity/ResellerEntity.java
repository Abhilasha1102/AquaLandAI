package com.landriskai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.math.BigDecimal;

/**
 * Reseller entity - represents reseller/agent accounts
 * Tracks commissions, wallets, and usage (FR-25, FR-26)
 */
@Entity
@Table(name = "lr_reseller", indexes = {
    @Index(name = "idx_reseller_user", columnList = "user_id"),
    @Index(name = "idx_reseller_status", columnList = "status"),
    @Index(name = "idx_reseller_created", columnList = "created_at")
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ResellerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the user account
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    /**
     * Business name / reseller name
     */
    @Column(nullable = false, length = 255)
    private String businessName;

    /**
     * Reseller code (unique, for tracking/identification)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String resellerCode;

    /**
     * Commission type: FIXED_PER_REPORT, PERCENTAGE, WALLET
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommissionType commissionType;

    /**
     * Commission value (based on commissionType)
     * e.g., for FIXED_PER_REPORT: 10 paise per report
     * for PERCENTAGE: 10 (means 10%)
     */
    @Column(nullable = false)
    private BigDecimal commissionValue;

    /**
     * Purchase mode: PAY_PER_REPORT, WALLET_TOPUP
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseMode purchaseMode;

    /**
     * Wallet balance in paise (for WALLET_TOPUP mode)
     */
    @Column(nullable = false)
    private Long walletBalancePaise = 0L;

    /**
     * Total reports generated through this reseller
     */
    @Column(nullable = false)
    private Long totalReportsGenerated = 0L;

    /**
     * Total earnings in paise
     */
    @Column(nullable = false)
    private Long totalEarningsPaise = 0L;

    /**
     * Reseller status: PENDING_APPROVAL, ACTIVE, SUSPENDED, REJECTED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResellerStatus status;

    /**
     * Approval notes (admin comment on approval/rejection)
     */
    @Column(length = 1000)
    private String approvalNotes;

    /**
     * Approved by admin user ID
     */
    private Long approvedByUserId;

    /**
     * Approved timestamp
     */
    private Instant approvedAt;

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

    public enum CommissionType {
        FIXED_PER_REPORT,    // Fixed amount per report (in paise)
        PERCENTAGE,          // Percentage of order amount
        WALLET              // Pre-loaded wallet (no commission, just debit from wallet)
    }

    public enum PurchaseMode {
        PAY_PER_REPORT,      // Reseller pays per report generated
        WALLET_TOPUP         // Reseller pre-loads wallet, consumed per report
    }

    public enum ResellerStatus {
        PENDING_APPROVAL,    // Awaiting admin approval
        ACTIVE,             // Approved and active
        SUSPENDED,          // Temporarily suspended
        REJECTED            // Application rejected
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
