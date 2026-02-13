package com.landriskai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Cache for previously searched land parcels
 * Stores search results for 7 days to enable quick reuse without reprocessing
 */
@Entity
@Table(name = "lr_search_cache", indexes = {
    @Index(name = "idx_khata_khesra", columnList = "khata, khesra"),
    @Index(name = "idx_location", columnList = "district, circle, village"),
    @Index(name = "idx_expires_at", columnList = "expiresAt"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCacheEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Land identifiers
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

    // Optional owner details for matching
    private String ownerName;
    private String plotArea;

    // Hash for quick duplicate detection
    @Column(nullable = false, unique = true, length = 64)
    private String searchHash;

    // Cached results
    @Column(columnDefinition = "TEXT")
    private String riskAnalysisJson;

    @Column(columnDefinition = "TEXT")
    private String findingsJson;

    // Risk metrics
    private String riskBand;
    private Integer riskScore;

    // PDF storage
    private String pdfPath;
    private Instant pdfGeneratedAt;

    // Cache validity
    @Column(nullable = false)
    private Instant expiresAt; // 7 days from now

    // Usage tracking
    @Builder.Default
    private Integer reusageCount = 0;
    private Instant lastReuseAt;
    @Builder.Default
    private Long totalRevenueFromReusagePaise = 0L; // Revenue generated from reuses

    // User identification for discount eligibility
    // Only same user (same email + whatsapp) gets discount
    @Column(length = 100)
    private String lastUserEmail;

    @Column(length = 20)
    private String lastUserWhatsapp;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        // Set expiry to 7 days from now
        this.expiresAt = Instant.now().plusSeconds(7 * 24 * 60 * 60);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    /**
     * Check if cache is still valid
     */
    public boolean isValid() {
        return Instant.now().isBefore(this.expiresAt);
    }

    /**
     * Calculate revenue impact - reduced fee for cached results
     * Full price: 2500 paise, Cached reuse: 500 paise (20% of original)
     */
    public static Integer calculateCachedOrderPrice() {
        return 500; // 20% of Rs 25 = Rs 5
    }
}
