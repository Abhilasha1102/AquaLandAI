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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        ReportEntity existing = reportRepo.findByOrder_Id(orderId).orElse(null);
        if (existing != null) {
            return ensureReferenceAndArtifacts(existing);
        }
        return generateAndDeliverInternal(orderId);
    }

    @Transactional
    public ReportEntity ensureReferenceAndArtifactsByReportId(Long reportId) throws Exception {
        ReportEntity report = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));
        return ensureReferenceAndArtifacts(report);
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
            .referenceNo("PENDING")
                .pdfPath("PENDING")
                .generatedAt(Instant.now())
                .summaryJson("{}")
                .build();

        report = saveWithReferenceNoRetry(report);

        String pdfPath = pdfReportService.generatePdf(order, result, report.getId(), verificationCode, report.getReferenceNo());

        report.setPdfPath(pdfPath);
        report.setSummaryJson(buildSummaryJson(report, order, result));
        report = reportRepo.save(report);

        // Cache the report for 7 days (with user identification for discount eligibility)
        // CRITICAL: Store email + whatsapp to track WHO created this cache
        // Only same user (same email + whatsapp) gets 80% discount on repeat searches
        // Different users pay full price (but can use cached PDF for faster delivery)
        saveToCache(order, report, result);

        // Construct links
        String base = props.getLinks().getBaseUrl();
        String downloadUrl = base + "/api/reports/" + report.getId() + "/download";
        String verifyUrl = base + "/api/reports/" + report.getId() + "/verify?code=" + report.getVerificationCode();

        // "Send" WhatsApp (mock)
        String khata = displayIdentifier(order.getKhata());
        String khesra = displayIdentifier(order.getKhesra());
        whatsAppService.sendReportLink(order.getWhatsappNumber(),
            "LandRiskAI report is ready. Ref: " + report.getReferenceNo() +
                ". Risk: " + report.getRiskBand() +
                ". Khata/Khesra: " + khata + " / " + khesra +
                ". Download: " + downloadUrl + " | Verify: " + verifyUrl);

        orderService.updateStatus(orderId, OrderStatus.DELIVERED);
        return report;
    }

    private ReportEntity ensureReferenceAndArtifacts(ReportEntity report) throws Exception {
        boolean referenceWasMissing = needsReferenceNo(report.getReferenceNo());
        if (referenceWasMissing) {
            report = saveWithReferenceNoRetry(report);
        }

        if (referenceWasMissing || needsPdfRefresh(report)) {
            OrderEntity order = report.getOrder();
            RiskResult result = riskEngine.assess(order);
            String verificationCode = report.getVerificationCode();
            if (verificationCode == null || verificationCode.isBlank()) {
                verificationCode = newVerificationCode();
                report.setVerificationCode(verificationCode);
            }

            String pdfPath = pdfReportService.generatePdf(
                    order,
                    result,
                    report.getId(),
                    verificationCode,
                    report.getReferenceNo()
            );

            report.setPdfPath(pdfPath);
            report.setSummaryJson(buildSummaryJson(report, order, result));
            report = reportRepo.save(report);
        }

        return report;
    }

    private String buildSummaryJson(ReportEntity report, OrderEntity order, RiskResult result) throws Exception {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("reportId", report.getId());
        summary.put("referenceNo", report.getReferenceNo());
        summary.put("generatedAt", report.getGeneratedAt().toString());
        summary.put("riskBand", report.getRiskBand().name());
        summary.put("riskScore", report.getRiskScore());
        summary.put("district", order.getDistrict());
        summary.put("circle", order.getCircle());
        summary.put("village", order.getVillage());
        summary.put("khata", order.getKhata());
        summary.put("khesra", order.getKhesra());
        summary.put("ownerName", order.getOwnerName());
        summary.put("ownershipType", order.getPlotArea());
        summary.put("plotArea", order.getPlotArea());
        summary.put("whatsappNumber", order.getWhatsappNumber());
        summary.put("emailAddress", order.getEmailAddress());
        summary.put("findingsCount", result.getFindings().size());
        return mapper.writeValueAsString(summary);
    }

    private boolean needsPdfRefresh(ReportEntity report) {
        String pdfPath = report.getPdfPath();
        if (pdfPath == null || pdfPath.isBlank() || "PENDING".equalsIgnoreCase(pdfPath)) {
            return true;
        }
        File file = new File(pdfPath);
        return !file.exists() || file.length() == 0;
    }

    /**
     * Save analysis result to cache for 7 days
     * IMPORTANT: Store lastUserEmail + lastUserWhatsapp to identify who created this cache
     * This enables user-based discount eligibility:
     * - Same user (email + whatsapp match): 80% discount (Rs 25 -> Rs 5)
     * - Different user: Full price (Rs 25) but can use cached PDF
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
            cache.setOwnerName(order.getOwnerName());
            cache.setPlotArea(order.getPlotArea());
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
                    .ownerName(order.getOwnerName())
                    .plotArea(order.getPlotArea())
                    .riskAnalysisJson(safeJson(result))  // Serialize entire RiskResult
                    .findingsJson(safeJson(result.getFindings()))
                    .riskBand(result.getBand().name())
                    .riskScore(result.getScore())
                    .pdfPath(report.getPdfPath())
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

    private ReportEntity saveWithReferenceNoRetry(ReportEntity report) {
        for (int attempt = 0; attempt < 3; attempt++) {
            String candidate = newReferenceNo();
            report.setReferenceNo(candidate);
            try {
                return reportRepo.save(report);
            } catch (DataIntegrityViolationException ex) {
                // Collision detected, retry
            }
        }
        throw new IllegalStateException("Unable to generate unique reference number");
    }

    private String newReferenceNo() {
        String date = DateTimeFormatter.BASIC_ISO_DATE.format(LocalDate.now(ZoneId.systemDefault()));
        return "LR-BR-" + date + "-" + randomBase32(6);
    }

    private boolean needsReferenceNo(String referenceNo) {
        return referenceNo == null || referenceNo.isBlank() || "PENDING".equalsIgnoreCase(referenceNo);
    }

    private String randomBase32(int length) {
        final String alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(idx));
        }
        return sb.toString();
    }

    private String displayIdentifier(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }
}

