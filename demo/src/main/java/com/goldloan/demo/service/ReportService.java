package com.goldloan.demo.service;

import com.goldloan.demo.entity.*;
import com.goldloan.demo.repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final LoanRepository loanRepository;
    private final InquiryRepository inquiryRepository;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // ================= EXCEL REPORTS =================

    public ByteArrayInputStream generateLoanReportExcel(LocalDate startDate, LocalDate endDate, LoanStatus status) {
        // LoanRepository expects LocalDate
        List<Loan> loans = loanRepository.findLoansForReport(status, startDate, endDate);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Loan Report");
            CellStyle headerStyle = createHeaderStyle(workbook, IndexedColors.DARK_BLUE);
            
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            String[] columns = {"Loan Number", "Customer Name", "Gold Weight (g)", "Karat", "Loan Amount",
                    "Interest Rate (%)", "Duration (months)", "Total Payable", "Amount Paid",
                    "Balance", "Status", "Start Date", "Maturity Date"};

            createHeaderRow(sheet, columns, headerStyle);

            int rowNum = 1;
            BigDecimal totalLoanAmount = BigDecimal.ZERO;
            BigDecimal totalPayable = BigDecimal.ZERO;
            BigDecimal totalPaid = BigDecimal.ZERO;

            for (Loan loan : loans) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(safeString(loan.getLoanNumber()));
                row.createCell(1).setCellValue(safeString(loan.getUser() != null ? loan.getUser().getFullName() : "N/A"));
                row.createCell(2).setCellValue(loan.getGoldWeight() != null ? loan.getGoldWeight().doubleValue() : 0);
                row.createCell(3).setCellValue(safeString(loan.getKarat() != null ? loan.getKarat().getDisplay() : "N/A"));

                Cell loanAmountCell = row.createCell(4);
                loanAmountCell.setCellValue(loan.getLoanAmount() != null ? loan.getLoanAmount().doubleValue() : 0);
                loanAmountCell.setCellStyle(currencyStyle);

                row.createCell(5).setCellValue(loan.getInterestRate() != null ? loan.getInterestRate().doubleValue() : 0);
                row.createCell(6).setCellValue(loan.getDurationMonths());

                Cell totalPayableCell = row.createCell(7);
                totalPayableCell.setCellValue(loan.getTotalPayable() != null ? loan.getTotalPayable().doubleValue() : 0);
                totalPayableCell.setCellStyle(currencyStyle);

                Cell amountPaidCell = row.createCell(8);
                amountPaidCell.setCellValue(loan.getAmountPaid() != null ? loan.getAmountPaid().doubleValue() : 0);
                amountPaidCell.setCellStyle(currencyStyle);

                Cell balanceCell = row.createCell(9);
                balanceCell.setCellValue(loan.getBalanceAmount() != null ? loan.getBalanceAmount().doubleValue() : 0);
                balanceCell.setCellStyle(currencyStyle);

                row.createCell(10).setCellValue(loan.getStatus() != null ? loan.getStatus().name() : "N/A");
                row.createCell(11).setCellValue(loan.getStartDate() != null ? loan.getStartDate().format(dateFormatter) : "N/A");
                row.createCell(12).setCellValue(loan.getMaturityDate() != null ? loan.getMaturityDate().format(dateFormatter) : "N/A");

                totalLoanAmount = totalLoanAmount.add(loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO);
                totalPayable = totalPayable.add(loan.getTotalPayable() != null ? loan.getTotalPayable() : BigDecimal.ZERO);
                totalPaid = totalPaid.add(loan.getAmountPaid() != null ? loan.getAmountPaid() : BigDecimal.ZERO);
            }

            createSummaryRow(sheet, rowNum, totalLoanAmount, totalPayable, totalPaid, currencyStyle);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report: " + e.getMessage(), e);
        }
    }

    public ByteArrayInputStream generateInquiryReportExcel(LocalDate startDate, LocalDate endDate, InquiryStatus status) {
        // InquiryRepository expects LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<Inquiry> inquiries = inquiryRepository.findInquiriesForReport(status, startDateTime, endDateTime);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inquiry Report");
            CellStyle headerStyle = createHeaderStyle(workbook, IndexedColors.GREY_25_PERCENT);

            String[] columns = {"ID", "Customer Name", "Email", "Phone", "Product Name",
                    "Message", "Status", "Date Created", "Responded By", "Response Date"};
            createHeaderRow(sheet, columns, headerStyle);

            int rowNum = 1;
            for (Inquiry inquiry : inquiries) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(inquiry.getId());
                row.createCell(1).setCellValue(safeString(inquiry.getUser() != null ? inquiry.getUser().getFullName() : "N/A"));
                row.createCell(2).setCellValue(safeString(inquiry.getUser() != null ? inquiry.getUser().getEmail() : "N/A"));
                row.createCell(3).setCellValue(safeString(inquiry.getContactNumber()));
                row.createCell(4).setCellValue(safeString(inquiry.getProduct() != null ? inquiry.getProduct().getName() : "N/A"));
                row.createCell(5).setCellValue(safeString(inquiry.getMessage()));
                row.createCell(6).setCellValue(inquiry.getStatus() != null ? inquiry.getStatus().name() : "N/A");
                row.createCell(7).setCellValue(inquiry.getCreatedAt() != null ? inquiry.getCreatedAt().format(dateFormatter) : "N/A");
                row.createCell(8).setCellValue(inquiry.getRespondedBy() != null ? inquiry.getRespondedBy().getFullName() : "N/A");
                row.createCell(9).setCellValue(inquiry.getRespondedAt() != null ? inquiry.getRespondedAt().format(dateFormatter) : "N/A");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Inquiry Excel report: " + e.getMessage(), e);
        }
    }

    // ================= PDF REPORT =================

    public ByteArrayInputStream generateLoanReportPDF(LocalDate startDate, LocalDate endDate, LoanStatus status) {
        List<Loan> loans = loanRepository.findLoansForReport(status, startDate, endDate);

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Gold Loan Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph info = new Paragraph(
                    String.format("Report Period: %s to %s | Status: %s | Generated: %s",
                            startDate.format(dateFormatter),
                            endDate.format(dateFormatter),
                            status != null ? status.name() : "ALL",
                            LocalDate.now().format(dateFormatter)),
                    infoFont
            );
            info.setAlignment(Element.ALIGN_CENTER);
            info.setSpacingAfter(20);
            document.add(info);

            PdfPTable table = new PdfPTable(11);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 1.5f, 1f, 0.8f, 1.2f, 0.8f, 0.8f, 1.2f, 1.2f, 1.2f, 0.8f});
            table.setSpacingBefore(10f);

            com.itextpdf.text.Font headerFontPDF = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
            String[] headers = {"Loan #", "Customer", "Weight", "Karat", "Amount", "Rate%",
                    "Months", "Payable", "Paid", "Balance", "Status"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFontPDF));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            BigDecimal totalLoanAmount = BigDecimal.ZERO;
            BigDecimal totalPayable = BigDecimal.ZERO;
            BigDecimal totalPaid = BigDecimal.ZERO;

            for (Loan loan : loans) {
                table.addCell(new Phrase(safeString(loan.getLoanNumber()), dataFont));
                table.addCell(new Phrase(safeString(loan.getUser() != null ? loan.getUser().getFullName() : "N/A"), dataFont));
                table.addCell(new Phrase(loan.getGoldWeight() != null ? loan.getGoldWeight() + "g" : "0g", dataFont));
                table.addCell(new Phrase(safeString(loan.getKarat() != null ? loan.getKarat().getDisplay() : "N/A"), dataFont));
                table.addCell(new Phrase("₹" + formatCurrency(loan.getLoanAmount()), dataFont));
                table.addCell(new Phrase(loan.getInterestRate() != null ? loan.getInterestRate() + "%" : "0%", dataFont));
                table.addCell(new Phrase(String.valueOf(loan.getDurationMonths()), dataFont));
                table.addCell(new Phrase("₹" + formatCurrency(loan.getTotalPayable()), dataFont));
                table.addCell(new Phrase("₹" + formatCurrency(loan.getAmountPaid()), dataFont));
                table.addCell(new Phrase("₹" + formatCurrency(loan.getBalanceAmount()), dataFont));

                PdfPCell statusCell = new PdfPCell(new Phrase(loan.getStatus() != null ? loan.getStatus().name() : "N/A", dataFont));
                if (loan.getStatus() == LoanStatus.ACTIVE) statusCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                else if (loan.getStatus() == LoanStatus.OVERDUE) statusCell.setBackgroundColor(BaseColor.ORANGE);
                table.addCell(statusCell);

                totalLoanAmount = totalLoanAmount.add(loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO);
                totalPayable = totalPayable.add(loan.getTotalPayable() != null ? loan.getTotalPayable() : BigDecimal.ZERO);
                totalPaid = totalPaid.add(loan.getAmountPaid() != null ? loan.getAmountPaid() : BigDecimal.ZERO);
            }

            // Summary row
            com.itextpdf.text.Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            addPdfSummaryRow(table, totalLoanAmount, totalPayable, totalPaid, summaryFont);

            document.add(table);

            Paragraph footer = new Paragraph(
                    "This is a system-generated report from Gold Palace Management System",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage(), e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // =================== Helper Methods ===================

    private String safeString(String str) {
        return str != null ? str : "N/A";
    }

    private String formatCurrency(BigDecimal amount) {
        return amount != null ? String.format("%,.2f", amount) : "0.00";
    }

    private CellStyle createHeaderStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("₹#,##0.00"));
        return style;
    }

    private void createHeaderRow(Sheet sheet, String[] columns, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }
    }

    private void createSummaryRow(Sheet sheet, int rowNum, BigDecimal totalLoan, BigDecimal totalPayable, BigDecimal totalPaid, CellStyle currencyStyle) {
        Row summaryRow = sheet.createRow(rowNum + 1);
        Cell summaryLabelCell = summaryRow.createCell(0);
        summaryLabelCell.setCellValue("TOTALS:");
        CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
        org.apache.poi.ss.usermodel.Font boldFont = sheet.getWorkbook().createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        summaryLabelCell.setCellStyle(boldStyle);

        Cell totalLoanCell = summaryRow.createCell(4);
        totalLoanCell.setCellValue(totalLoan.doubleValue());
        totalLoanCell.setCellStyle(currencyStyle);

        Cell totalPayableCell = summaryRow.createCell(7);
        totalPayableCell.setCellValue(totalPayable.doubleValue());
        totalPayableCell.setCellStyle(currencyStyle);

        Cell totalPaidCell = summaryRow.createCell(8);
        totalPaidCell.setCellValue(totalPaid.doubleValue());
        totalPaidCell.setCellStyle(currencyStyle);
    }

    private void addPdfSummaryRow(PdfPTable table, BigDecimal totalLoan, BigDecimal totalPayable, BigDecimal totalPaid, com.itextpdf.text.Font font) {
        PdfPCell summaryCell = new PdfPCell(new Phrase("TOTALS:", font));
        summaryCell.setColspan(4);
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        summaryCell.setPadding(5);
        table.addCell(summaryCell);

        PdfPCell totalLoanCell = new PdfPCell(new Phrase("₹" + formatCurrency(totalLoan), font));
        totalLoanCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(totalLoanCell);

        PdfPCell emptyCell = new PdfPCell(new Phrase("", font));
        emptyCell.setColspan(2);
        emptyCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(emptyCell);

        PdfPCell totalPayableCell = new PdfPCell(new Phrase("₹" + formatCurrency(totalPayable), font));
        totalPayableCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(totalPayableCell);

        PdfPCell totalPaidCell = new PdfPCell(new Phrase("₹" + formatCurrency(totalPaid), font));
        totalPaidCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(totalPaidCell);

        BigDecimal totalBalance = totalPayable.subtract(totalPaid);
        PdfPCell totalBalanceCell = new PdfPCell(new Phrase("₹" + formatCurrency(totalBalance), font));
        totalBalanceCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(totalBalanceCell);

        PdfPCell lastEmptyCell = new PdfPCell(new Phrase(""));
        lastEmptyCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(lastEmptyCell);
    }
}
