package mu.rekolt.service;

import mu.rekolt.model.Delivery;
import mu.rekolt.model.Member;
import mu.rekolt.model.SeasonReport;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;


public class ReportWriterService {

    public static void generateDocument(SeasonReport report, String docxPath) throws IOException {
        Path outputPath = Paths.get(docxPath);
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }

        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(outputPath.toFile())) {

            writeTitleSection(document);

            for (Member member : report.getMembers()) {
                XWPFParagraph pageBreak = document.createParagraph();
                pageBreak.setPageBreak(true);
                writeMemberSection(document, member);
            }

            writeClosingSection(document, report);
            document.write(out);
        }

        appendRunLog(report);
    }

    private static void writeMemberSection(XWPFDocument document, Member member) {
        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Member " + member.getMemberId() + " - " + member.getName());
        titleRun.setBold(true);
        titleRun.setFontSize(14);

        XWPFTable table = document.createTable(1, 5);
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("Produce");
        header.getCell(1).setText("Mass (kg)");
        header.getCell(2).setText("Grade");
        header.getCell(3).setText("Week");
        header.getCell(4).setText("Net Payable (MUR)");

        for (Delivery delivery : member.getDeliveries()) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(delivery.getProduceCode());
            row.getCell(1).setText(String.format("%.1f", delivery.getMassKg()));
            row.getCell(2).setText(delivery.getGrade().toString());
            row.getCell(3).setText(String.valueOf(delivery.getWeek()));
            row.getCell(4).setText(String.format("%.2f", delivery.getNetPayable()));
        }

        XWPFParagraph totalPara = document.createParagraph();
        XWPFRun totalRun = totalPara.createRun();
        totalRun.setBold(true);
        totalRun.setText(String.format("NET PAYABLE: %.2f MUR", member.getNetPayable()));

        XWPFParagraph signaturePara = document.createParagraph();
        signaturePara.setSpacingBefore(400);
        XWPFRun signatureRun = signaturePara.createRun();
        signatureRun.setText("Signature: ____________________________");
    }

    private static void writeClosingSection(XWPFDocument document, SeasonReport report) {
        XWPFParagraph pageBreak = document.createParagraph();
        pageBreak.setPageBreak(true);

        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Season Summary");
        titleRun.setBold(true);
        titleRun.setFontSize(14);

        XWPFParagraph totalPara = document.createParagraph();
        XWPFRun totalRun = totalPara.createRun();
        totalRun.setBold(true);
        totalRun.setText(String.format("SEASON TOTAL: %.2f MUR across %d members",
                report.getSeasonTotal(), report.getMembers().size()));
    }

    private static void appendRunLog(SeasonReport report) throws IOException {
        Path logPath = Paths.get("output/run-log.txt");
        if (logPath.getParent() != null) {
            Files.createDirectories(logPath.getParent());
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = String.format("%s - generated report for %d members, season total %.2f MUR%n",
                timestamp, report.getMembers().size(), report.getSeasonTotal());

        try (var writer = Files.newBufferedWriter(logPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
        }
    }

    private static void writeTitleSection(XWPFDocument document) {
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText("REKOLT Planters' Cooperative - Season Payment Report");
        titleRun.setBold(true);
        titleRun.setFontSize(18);

        XWPFParagraph subtitle = document.createParagraph();
        subtitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun subtitleRun = subtitle.createRun();
        subtitleRun.setText("Payment statement for each member who delivered produce this season, followed by the season total.");
        subtitleRun.setItalic(true);
        subtitleRun.setFontSize(11);
    }
}