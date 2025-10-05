package com.goldloan.demo.controller;


import com.goldloan.demo.dto.InquiryDto;
import com.goldloan.demo.dto.InquiryResponseDto;
import com.goldloan.demo.dto.InquiryResponseRequestDto;
import com.goldloan.demo.entity.Inquiry;
import com.goldloan.demo.entity.User;
import com.goldloan.demo.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> createInquiry(@RequestBody InquiryDto inquiryDto, Authentication auth) {
        User user = (User) auth.getPrincipal();
        inquiryService.createInquiry(inquiryDto, user.getId());
        return ResponseEntity.ok("Inquiry submitted successfully");
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<InquiryResponseDto>> getUserInquiries(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Inquiry> inquiries = inquiryService.getUserInquiries(user.getId());
        
        List<InquiryResponseDto> response = inquiries.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InquiryResponseDto>> getAllInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Inquiry> inquiries = inquiryService.getAllInquiries(pageable);
        Page<InquiryResponseDto> response = inquiries.map(this::convertToDto);
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/respond")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> respondToInquiry(
            @PathVariable Long id,
            @RequestBody InquiryResponseRequestDto responseDto,
            Authentication auth) {
        
        User admin = (User) auth.getPrincipal();
        inquiryService.respondToInquiry(id, responseDto.getResponse(), admin.getUsername());
        return ResponseEntity.ok("Response sent successfully");
    }
    
    private InquiryResponseDto convertToDto(Inquiry inquiry) {
    return InquiryResponseDto.builder()
        .id(inquiry.getId())
        .userName(inquiry.getUser().getFullName())
        .productName(inquiry.getProduct().getName())
        .message(inquiry.getMessage())
        .contactNumber(inquiry.getContactNumber())
        .status(inquiry.getStatus())
        .adminResponse(inquiry.getAdminResponse())
        .createdAt(inquiry.getCreatedAt())
        .respondedAt(inquiry.getRespondedAt())
        .respondedBy(
            inquiry.getRespondedBy() != null ? inquiry.getRespondedBy().getUsername() : null
        )
        .build();
}

}
