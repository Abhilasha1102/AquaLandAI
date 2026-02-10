package com.landriskai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Audit log entity - tracks all admin actions for compliance (NFR-7)
 * GDPR/privacy-compliant activity logging
 */
@Entity
@Table(name = "lr_audit_log", indexes = {
    @Index(name = "idx_audit_admin_id", columnList = "admin_user_id"),
    @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_audit_timestamp", columnList = "created_at"),
    @Index(name = "idx_audit_action", columnList = "action_type")
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Admin user ID who performed the action
     */
    @Column(nullable = false)
    private Long adminUserId;

    /**
     * Admin username/email for reference
     */
    @Column(nullable = false, length = 255)
    private String adminUsername;

    /**
     * Action type: CREATE, UPDATE, DELETE, VIEW, RESEND_REPORT, MARK_REFUND, etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    /**
     * Entity type affected: ORDER, REPORT, USER, RESELLER, etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    /**
     * Entity ID (e.g., order ID, report ID)
     */
    @Column(nullable = false)
    private Long entityId;

    /**
     * Old value (for auditing changes)
     */
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    /**
     * New value (for auditing changes)
     */
    @Column(columnDefinition = "TEXT")
    private String newValue;

    /**
     * Reason for action (e.g., "Customer request", "Duplicate entry", etc.)
     */
    @Column(length = 500)
    private String reason;

    /**
     * IP address of the admin
     */
    @Column(length = 50)
    private String ipAddress;

    /**
     * User agent / client info
     */
    @Column(length = 500)
    private String userAgent;

    /**
     * Status of the action: SUCCESS, FAILED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionStatus status = ActionStatus.SUCCESS;

    /**
     * Error message if action failed
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Created timestamp (when action was performed)
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public enum ActionType {
        CREATE,
        UPDATE,
        DELETE,
        VIEW,
        RESEND_REPORT,
        MARK_REFUND,
        REGENERATE_REPORT,
        APPROVE_RESELLER,
        REJECT_RESELLER,
        SUSPEND_USER,
        EXPORT_DATA
    }

    public enum EntityType {
        ORDER,
        REPORT,
        USER,
        RESELLER,
        PAYMENT,
        NOTIFICATION
    }

    public enum ActionStatus {
        SUCCESS,
        FAILED
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
