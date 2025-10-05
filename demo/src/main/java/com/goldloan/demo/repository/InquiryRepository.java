package com.goldloan.demo.repository;

import com.goldloan.demo.entity.Inquiry;
import com.goldloan.demo.entity.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Inquiry> findByStatusOrderByCreatedAtDesc(InquiryStatus status);

    Page<Inquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT i FROM Inquiry i WHERE " +
           "(:status IS NULL OR i.status = :status) " +
           "AND (:startDate IS NULL OR i.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR i.createdAt <= :endDate)")
    List<Inquiry> findInquiriesForReport(
        @Param("status") InquiryStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
