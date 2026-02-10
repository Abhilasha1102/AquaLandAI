package com.landriskai.pdf;

import com.landriskai.config.LandRiskAiProperties;
import com.landriskai.entity.OrderEntity;
import com.landriskai.risk.RiskFinding;
import com.landriskai.risk.RiskResult;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class PdfReportService {

    private final LandRiskAiProperties props;

    public PdfReportService(LandRiskAiProperties props) {
        this.props = props;
    }

    public String generatePdf(OrderEntity order, RiskResult result, Long reportId, String verificationCode) throws Exception {
        File dir = new File(props.getStorage().getReportDir());
        if (!dir.exists()) Files.createDirectories(dir.toPath());

        String filename = "LandRiskAI_Report_" + reportId + ".pdf";
        File out = new File(dir, filename);

        try (FileOutputStream fos = new FileOutputStream(out)) {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, fos);
            doc.open();

            Font h1 = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font h2 = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font body = new Font(Font.HELVETICA, 10, Font.NORMAL);

            doc.add(new Paragraph("LandRiskAI - Bihar Land Risk Report (MVP Demo)", h1));
            doc.add(new Paragraph("Report ID: " + reportId + " | Verification Code: " + verificationCode, body));
            doc.add(new Paragraph("Generated: " + DateTimeFormatter.ISO_INSTANT.format(Instant.now()), body));
            doc.add(Chunk.NEWLINE);

            // Summary block
            doc.add(new Paragraph("Summary", h2));
            doc.add(new Paragraph("Risk Band: " + result.getBand(), body));
            doc.add(new Paragraph("Risk Score (0-100): " + result.getScore(), body));
            doc.add(Chunk.NEWLINE);

            // Parcel snapshot
            doc.add(new Paragraph("Parcel Snapshot", h2));
            doc.add(new Paragraph("District: " + order.getDistrict(), body));
            doc.add(new Paragraph("Circle/Block: " + order.getCircle(), body));
            doc.add(new Paragraph("Village/Mauza: " + order.getVillage(), body));
            doc.add(new Paragraph("Khata: " + order.getKhata() + " | Khesra: " + order.getKhesra(), body));
            if (order.getOwnerName() != null) doc.add(new Paragraph("Owner (input): " + order.getOwnerName(), body));
            if (order.getPlotArea() != null) doc.add(new Paragraph("Area (input): " + order.getPlotArea(), body));
            doc.add(Chunk.NEWLINE);

            // Findings
            doc.add(new Paragraph("Findings & Evidence", h2));
            PdfPTable table = new PdfPTable(new float[]{2, 3, 2, 2});
            table.setWidthPercentage(100);
            table.addCell(headerCell("Finding"));
            table.addCell(headerCell("Message / Evidence"));
            table.addCell(headerCell("Source"));
            table.addCell(headerCell("Confidence"));

            if (result.getFindings().isEmpty()) {
                table.addCell(cell("No findings"));
                table.addCell(cell("No issues detected by MVP rules."));
                table.addCell(cell("N/A"));
                table.addCell(cell("N/A"));
            } else {
                for (RiskFinding f : result.getFindings()) {
                    table.addCell(cell(f.getTitle() + " (" + f.getSeverity() + ")"));
                    table.addCell(cell(f.getMessage() + "\nEvidence: " + safe(f.getEvidence())));
                    table.addCell(cell(safe(f.getSource())));
                    table.addCell(cell(safe(f.getConfidence())));
                }
            }
            doc.add(table);
            doc.add(Chunk.NEWLINE);

            // Next steps
            doc.add(new Paragraph("Recommended Next Steps (Buyer Checklist)", h2));
            com.lowagie.text.List list = new com.lowagie.text.List(false, 12);
            list.add(new ListItem("Verify the exact Khata/Khesra and area in official records for this village/circle.", body));
            list.add(new ListItem("Ask seller for chain documents (sale deed/partition/mutation receipts) and validate continuity.", body));
            list.add(new ListItem("Check for any encumbrance or dispute indicators via local verification (registry/court search).", body));
            list.add(new ListItem("If risk band is AMBER/RED, consider professional verification before paying advance.", body));
            doc.add(list);
            doc.add(Chunk.NEWLINE);

            // Disclaimer
            doc.add(new Paragraph("Disclaimer", h2));
            doc.add(new Paragraph(
                    "This report is for informational purposes only and is NOT a legal title certificate or legal advice. " +
                    "It is generated based on the inputs provided and data availability at the time of generation. " +
                    "Users must independently verify all details with relevant authorities and documents.",
                    body));

            doc.close();
        }

        return out.getAbsolutePath();
    }

    private String safe(String s) { return s == null ? "" : s; }

    private com.lowagie.text.pdf.PdfPCell headerCell(String t) {
        Font f = new Font(Font.HELVETICA, 10, Font.BOLD);
        com.lowagie.text.pdf.PdfPCell c = new com.lowagie.text.pdf.PdfPCell(new Phrase(t, f));
        c.setPadding(6);
        return c;
    }

    private com.lowagie.text.pdf.PdfPCell cell(String t) {
        Font f = new Font(Font.HELVETICA, 9, Font.NORMAL);
        com.lowagie.text.pdf.PdfPCell c = new com.lowagie.text.pdf.PdfPCell(new Phrase(t, f));
        c.setPadding(6);
        return c;
    }
}
