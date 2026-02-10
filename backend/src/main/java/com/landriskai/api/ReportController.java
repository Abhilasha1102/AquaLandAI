package com.landriskai.api;

import com.landriskai.entity.ReportEntity;
import com.landriskai.repo.ReportRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportRepository reportRepo;

    public ReportController(ReportRepository reportRepo) {
        this.reportRepo = reportRepo;
    }

    @GetMapping("/{reportId}/download")
    public ResponseEntity<FileSystemResource> download(@PathVariable Long reportId) {
        ReportEntity report = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));

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
                .orElseThrow(() -> new IllegalArgumentException("Invalid reportId/code"));

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(report.getSummaryJson());
    }
}
