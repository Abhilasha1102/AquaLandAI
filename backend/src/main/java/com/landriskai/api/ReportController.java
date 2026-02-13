package com.landriskai.api;

import com.landriskai.entity.ReportEntity;
import com.landriskai.repo.ReportRepository;
import com.landriskai.service.ReportService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportRepository reportRepo;
    private final ReportService reportService;

    public ReportController(ReportRepository reportRepo, ReportService reportService) {
        this.reportRepo = reportRepo;
        this.reportService = reportService;
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportSummaryResponse> getReportSummary(@PathVariable Long reportId) {
        ReportEntity report;
        try {
            report = reportService.ensureReferenceAndArtifactsByReportId(reportId);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load report: " + reportId);
        }

        return ResponseEntity.ok(new ReportSummaryResponse(
            report.getId(),
            report.getReferenceNo(),
            report.getSummaryJson()
        ));
    }

    @GetMapping("/{reportId}/download")
    public ResponseEntity<FileSystemResource> download(@PathVariable Long reportId) {
        ReportEntity report;
        try {
            report = reportService.ensureReferenceAndArtifactsByReportId(reportId);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to prepare download for report: " + reportId);
        }

        FileSystemResource res = new FileSystemResource(report.getPdfPath());
        String filename = "LandRiskAI_Report_" + reportId + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(res);
    }

    @GetMapping("/{reportId}/verify")
    public ResponseEntity<String> verify(@PathVariable Long reportId, @RequestParam String code) {
        ReportEntity report = reportRepo.findByIdAndVerificationCode(reportId, code)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid reportId/code"));

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(report.getSummaryJson());
    }

        @GetMapping("/reference/{referenceId}")
        public ResponseEntity<ReportByRefResponse> getByReference(@PathVariable String referenceId) {
            return getByRef(referenceId);
        }

        @GetMapping("/by-ref/{ref}")
        public ResponseEntity<ReportByRefResponse> getByRef(@PathVariable String ref) {
            String normalized = ref != null ? ref.trim().toUpperCase() : "";
            ReportEntity report = reportRepo.findByReferenceNo(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found: " + ref));

            String downloadUrl = "/api/reports/" + report.getId() + "/download";
            String verifyUrl = "/api/reports/" + report.getId() + "/verify?code=" + report.getVerificationCode();

            return ResponseEntity.ok(new ReportByRefResponse(
                report.getId(),
                report.getReferenceNo(),
                downloadUrl,
                verifyUrl,
                report.getSummaryJson()
            ));
        }

        @GetMapping("/by-ref/{ref}/download")
        public ResponseEntity<FileSystemResource> downloadByRef(@PathVariable String ref) {
            String normalized = ref != null ? ref.trim().toUpperCase() : "";
            ReportEntity report = reportRepo.findByReferenceNo(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found: " + ref));
            return download(report.getId());
        }

        public record ReportByRefResponse(
            Long reportId,
            String referenceNo,
            String downloadUrl,
            String verifyUrl,
            String summaryJson
        ) {}

        public record ReportSummaryResponse(
            Long reportId,
            String referenceNo,
            String summaryJson
        ) {}
}
