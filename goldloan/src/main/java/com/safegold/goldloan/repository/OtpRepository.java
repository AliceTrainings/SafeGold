package com.safegold.goldloan.repository;

import com.safegold.goldloan.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByMobileNumber(String mobileNumber);
}

