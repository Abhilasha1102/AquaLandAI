package com.landriskai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landriskai.config.LandRiskAiProperties;
import com.landriskai.domain.OrderStatus;
import com.landriskai.entity.OrderEntity;
import com.landriskai.entity.ReportEntity;
import com.landriskai.entity.SearchCacheEntity;
import com.landriskai.notify.WhatsAppService;
import com.landriskai.pdf.PdfReportService;
import com.landriskai.repo.ReportRepository;
import com.landriskai.repo.SearchCacheRepository;
import com.landriskai.risk.RiskEngine;
import com.landriskai.risk.RiskResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ReportService {

    private final ReportRepository reportRepo;
    private final OrderService orderService;
    private final RiskEngine riskEngine;
    private final PdfReportService pdfReportService;
    private final LandRiskAiProperties props;
    private final WhatsAppService whatsAppService;
    private final SearchCacheRepository searchCacheRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final SecureRandom random = new SecureRandom();

    public ReportService(
            ReportRepository reportRepo,
            OrderService orderService,
            RiskEngine riskEngine,
            PdfReportService pdfReportService,
            LandRiskAiProperties props,
            WhatsAppService whatsAppService,
            SearchCacheRepository searchCacheRepository
    ) {
        this.reportRepo = reportRepo;
        this.orderService = orderService;
        this.riskEngine = riskEngine;
        this.pdfReportService = pdfReportService;
        this.props = props;
        this.whatsAppService = whatsAppService;
        this.searchCacheRepository = searchCacheRepository;
    }

    @Transactional
    public ReportEntity generateAndDeliver(Long orderId) throws Exception {
        // Idempotent: if report exists, return it
        return reportRepo.findByOrder_Id(orderId).orElseGet(() -> {
            try {
                return generateAndDeliverInternal(orderId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private ReportEntity generateAndDeliverInternal(Long orderId) throws Exception {
        OrderEntity order = orderService.updateStatus(orderId, OrderStatus.GENERATING);

        RiskResult result = riskEngine.assess(order);
        String verificationCode = newVerificationCode();

        // Save report first to get reportId
        ReportEntity report = ReportEntity.builder()
                .order(order)
                .riskBand(result.getBand())
                .riskScore(result.getScore())
                .verificationCode(verificationCode)
                .pdfPath("PENDING")
                .generatedAt(Instant.now())
                .summaryJson("{}")
                .build();

        report = reportRepo.save(report);

        String pdfPath = pdfReportService.generatePdf(order, result, report.getId(), verificationCode);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("reportId", report.getId());
        summary.put("generatedAt", report.getGeneratedAt().toString());
        summary.put("riskBand", report.getRiskBand().name());
        summary.put("riskScore", report.getRiskScore());
        summary.put("district", order.getDistrict());
        summary.put("circle", order.getCircle());
        summary.put("village", order.getVillage());
        summary.put("khata", order.getKhata());
        summary.put("khesra", order.getKhesra());
        summary.put("findingsCount", result.getFindings().size());

        report.setPdfPath(pdfPath);
        report.setSummaryJson(mapper.writeValueAsString(summary));
        report = reportRepo.save(report);

        // Cache the report for 7 days (with user identification for discount eligibility)
        // CRITICAL: Store email + whatsapp to track WHO created this cache
        // Only same user (same email + whatsapp) gets 80% discount on repeat searches
        // Different users pay full price ₹25 (but can use cached PDF for faster delivery)
        saveToCache(order, report, result);

        // Construct links
        String base = props.getLinks().getBaseUrl();
        String downloadUrl = base + "/api/reports/" + report.getId() + "/download";
        String verifyUrl = base + "/api/reports/" + report.getId() + "/verify?code=" + report.getVerificationCode();

        // "Send" WhatsApp (mock)
        whatsAppService.sendReportLink(order.getWhatsappNumber(),
                "LandRiskAI report is ready. Risk: " + report.getRiskBand() +
                        ". Download: " + downloadUrl + " | Verify: " + verifyUrl);

        orderService.updateStatus(orderId, OrderStatus.DELIVERED);
        return report;
    }

    /**
     * Save analysis result to cache for 7 days
     * IMPORTANT: Store lastUserEmail + lastUserWhatsapp to identify who created this cache
     * This enables user-based discount eligibility:
     * - Same user (email + whatsapp match): 80% discount (₹25 → ₹5)
     * - Different user: Full price (₹25) but can use cached PDF
     */
    private void saveToCache(OrderEntity order, ReportEntity report, RiskResult result) {
        SearchCacheEntity existingCache = searchCacheRepository.findValidByLandIdentifiers(
                order.getKhata(), order.getKhesra(), order.getDistrict());

        SearchCacheEntity cache;
        if (existingCache != null) {
            // Update existing cache with new user info
            cache = existingCache;
            cache.setLastUserEmail(order.getEmailAddress() != null ? order.getEmailAddress() : "unknown");
            cache.setLastUserWhatsapp(order.getWhatsappNumber());
            cache.setLastReuseAt(Instant.now());
            cache.setReusageCount(cache.getReusageCount() + 1);
        } else {
            // Create new cache entry with this user's info
            cache = SearchCacheEntity.builder()
                    .khata(order.getKhata())
                    .khesra(order.getKhesra())
                    .district(order.getDistrict())
                    .circle(order.getCircle())
                    .village(order.getVillage())
                    .searchHash(generateSearchHash(order))
                    .lastUserEmail(order.getEmailAddress() != null ? order.getEmailAddress() : "unknown")
                    .lastUserWhatsapp(order.getWhatsappNumber())
                    .riskAnalysisJson(safeJson(result))  // Serialize entire RiskResult
                    .findingsJson(safeJson(result.getFindings()))
                    .riskBand(result.getBand().name())
                    .riskScore(result.getScore())
                    .pdfPath(report.getPdfPath())
                    .reusageCount(0)
                    .totalRevenueFromReusagePaise(0L)
                    .build();
            // expiresAt is set automatically in @PrePersist to now() + 7 days
        }

        searchCacheRepository.save(cache);
    }

    private String generateSearchHash(OrderEntity order) {
        String key = order.getKhata() + "|" + order.getKhesra() + "|" + order.getDistrict();
        return Integer.toHexString(key.hashCode());
    }

    private String safeJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String newVerificationCode() {
        byte[] b = new byte[6];
        random.nextBytes(b);
        return HexFormat.of().formatHex(b);
    }
}
