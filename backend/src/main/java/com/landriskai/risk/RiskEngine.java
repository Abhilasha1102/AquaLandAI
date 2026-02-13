package com.landriskai.risk;

import com.landriskai.domain.RiskBand;
import com.landriskai.entity.OrderEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskEngine {

    /**
     * MVP rules:
     * - If ownerName is missing -> warning (+15)
     * - If plotArea missing -> info (+5)
     * - If khata/khesra not numeric-ish -> warning (+10)
     * - Example "hotspot" demo: if district contains "patna" -> info (+5)
     */
    public RiskResult assess(OrderEntity order) {
        int score = 10; // base risk
        List<RiskFinding> findings = new ArrayList<>();

        if (order.getOwnerName() == null || order.getOwnerName().isBlank()) {
            score += 15;
            findings.add(RiskFinding.builder()
                    .code("OWN_MISSING")
                    .title("Owner name not provided")
                    .message("Owner name was not provided, so identity matching confidence is reduced.")
                    .severity(FindingSeverity.WARNING)
                    .evidence("Input missing")
                    .source("User input")
                    .confidence("LOW")
                    .build());
        }

        if (order.getPlotArea() == null || order.getPlotArea().isBlank()) {
            score += 5;
            findings.add(RiskFinding.builder()
                    .code("AREA_MISSING")
                    .title("Plot area not provided")
                    .message("Area mismatch checks are limited because plot area is not provided.")
                    .severity(FindingSeverity.INFO)
                    .evidence("Input missing")
                    .source("User input")
                    .confidence("LOW")
                    .build());
        }

        String khata = order.getKhata();
        String khesra = order.getKhesra();
        boolean khataProvided = khata != null && !khata.isBlank();
        boolean khesraProvided = khesra != null && !khesra.isBlank();
        boolean khataInvalid = khataProvided && !khata.matches("^[0-9A-Za-z\\-/]+$");
        boolean khesraInvalid = khesraProvided && !khesra.matches("^[0-9A-Za-z\\-/]+$");

        if (khataInvalid || khesraInvalid) {
            score += 10;
            findings.add(RiskFinding.builder()
                    .code("ID_FORMAT")
                    .title("Khata/Khesra format looks unusual")
                    .message("Khata/Khesra includes uncommon characters. Verify the identifiers are correct.")
                    .severity(FindingSeverity.WARNING)
                .evidence("khata=" + (khataProvided ? khata : "N/A") + ", khesra=" + (khesraProvided ? khesra : "N/A"))
                    .source("Input validation heuristics")
                    .confidence("MEDIUM")
                    .build());
        }

        if (order.getDistrict() != null && order.getDistrict().toLowerCase().contains("patna")) {
            score += 5;
            findings.add(RiskFinding.builder()
                    .code("DEMO_CONTEXT")
                    .title("Higher activity region (demo)")
                    .message("This is a demo context rule. Replace with real location risk datasets later.")
                    .severity(FindingSeverity.INFO)
                    .evidence("district=" + order.getDistrict())
                    .source("Demo rule")
                    .confidence("LOW")
                    .build());
        }

        RiskBand band;
        if (score >= 60) band = RiskBand.RED;
        else if (score >= 30) band = RiskBand.AMBER;
        else band = RiskBand.GREEN;

        return RiskResult.builder()
                .score(Math.min(score, 100))
                .band(band)
                .findings(findings)
                .build();
    }
}
