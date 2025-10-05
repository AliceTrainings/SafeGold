package com.goldloan.demo.service;

import com.goldloan.demo.dto.InquiryDto;
import com.goldloan.demo.entity.Inquiry;
import com.goldloan.demo.entity.InquiryStatus;
import com.goldloan.demo.entity.Product;
import com.goldloan.demo.entity.User;
import com.goldloan.demo.repository.InquiryRepository;
import com.goldloan.demo.repository.ProductRepository;
import com.goldloan.demo.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Inquiry createInquiry(InquiryDto inquiryDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Product product = productRepository.findById(inquiryDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .product(product)
                .message(inquiryDto.getMessage())
                .contactNumber(inquiryDto.getContactNumber())
                .status(InquiryStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getUserInquiries(Long userId) {
        return inquiryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Page<Inquiry> getAllInquiries(Pageable pageable) {
        return inquiryRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Inquiry respondToInquiry(Long inquiryId, String response, String respondedByUsername) {
    Inquiry inquiry = inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));

    User responder = userRepository.findByUsername(respondedByUsername)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    inquiry.setAdminResponse(response);
    inquiry.setStatus(InquiryStatus.RESPONDED);
    inquiry.setRespondedBy(responder);
    inquiry.setRespondedAt(LocalDateTime.now());

    return inquiryRepository.save(inquiry);
}
}