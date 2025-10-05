package com.goldloan.demo.controller;

import com.goldloan.demo.entity.InquiryStatus;
import com.goldloan.demo.entity.LoanStatus;
import com.goldloan.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Loan Excel
    @GetMapping("/report/loan/excel")
    public ResponseEntity<InputStreamResource> getLoanReportExcel(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) LoanStatus status) {

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        ByteArrayInputStream in = reportService.generateLoanReportExcel(start, end, status);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=loan_report.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    // Loan PDF
    @GetMapping("/report/loan/pdf")
    public ResponseEntity<InputStreamResource> getLoanReportPDF(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) LoanStatus status) {

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        ByteArrayInputStream in = reportService.generateLoanReportPDF(start, end, status);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=loan_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(in));
    }

    // Inquiry Excel
    @GetMapping("/report/inquiry/excel")
    public ResponseEntity<InputStreamResource> getInquiryReportExcel(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) InquiryStatus status) {

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        ByteArrayInputStream in = reportService.generateInquiryReportExcel(start, end, status);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=inquiry_report.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
