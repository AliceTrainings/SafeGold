package com.goldloan.safegold1.service;

import com.goldloan.safegold1.model.Loan;
import com.goldloan.safegold1.model.Payment;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {

    public byte[] buildLoansAndPaymentsPdf(List<Loan> loans, String title) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph(title));
            document.add(new Paragraph(" "));

            for (Loan loan : loans) {
                document.add(new Paragraph("Loan ID: " + loan.getId()));
                if (loan.getStartDate() != null) {
                    document.add(new Paragraph("Start Date: " + loan.getStartDate()));
                }
                document.add(new Paragraph("Status: " + nullSafe(loan.getStatus())));
                document.add(new Paragraph("Amount: " + loan.getAmount()));
                if (loan.getInterestRate() != null) {
                    document.add(new Paragraph("Interest Rate: " + loan.getInterestRate() + "%"));
                }
                if (loan.getTenure() != null) {
                    document.add(new Paragraph("Tenure (months): " + loan.getTenure()));
                }
                if (loan.getGoldDescription() != null) {
                    document.add(new Paragraph("Gold: " + loan.getGoldDescription()));
                }

                List<Payment> payments = loan.getPayments();
                if (payments != null && !payments.isEmpty()) {
                    document.add(new Paragraph("Payments:"));
                    PdfPTable table = new PdfPTable(5);
                    table.addCell("Payment ID");
                    table.addCell("Amount");
                    table.addCell("Remaining");
                    table.addCell("Status");
                    table.addCell("Date");
                    for (Payment p : payments) {
                        table.addCell(String.valueOf(p.getId()));
                        table.addCell(String.valueOf(p.getAmount()));
                        table.addCell(String.valueOf(p.getRemaining()));
                        table.addCell(nullSafe(p.getStatus()));
                        table.addCell(String.valueOf(p.getDate()));
                    }
                    document.add(table);
                } else {
                    document.add(new Paragraph("No payments yet."));
                }

                document.add(new Paragraph(" "));
            }
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to export PDF", e);
        } finally {
            document.close();
        }
        return outputStream.toByteArray();
    }

    private String nullSafe(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
}


