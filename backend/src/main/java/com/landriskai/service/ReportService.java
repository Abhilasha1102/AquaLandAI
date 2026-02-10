package com.landriskai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landriskai.config.LandRiskAiProperties;
import com.landriskai.domain.OrderStatus;
import com.landriskai.entity.OrderEntity;
import com.landriskai.entity.ReportEntity;
import com.landriskai.notify.WhatsAppService;
import com.landriskai.pdf.PdfReportService;
import com.landriskai.repo.ReportRepository;
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
    private final ObjectMapper mapper = new ObjectMapper();
    private final SecureRandom random = new SecureRandom();

    public ReportService(
            ReportRepository reportRepo,
            OrderService orderService,
            RiskEngine riskEngine,
            PdfReportService pdfReportService,
            LandRiskAiProperties props,
            WhatsAppService whatsAppService
    ) {
        this.reportRepo = reportRepo;
        this.orderService = orderService;
        this.riskEngine = riskEngine;
        this.pdfReportService = pdfReportService;
        this.props = props;
        this.whatsAppService = whatsAppService;
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

    private String newVerificationCode() {
        byte[] b = new byte[6];
        random.nextBytes(b);
        return HexFormat.of().formatHex(b);
    }
}
