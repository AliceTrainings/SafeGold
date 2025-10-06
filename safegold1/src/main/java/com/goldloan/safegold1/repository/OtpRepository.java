package com.goldloan.safegold1.repository;

import com.goldloan.safegold1.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByMobileNumber(String mobileNumber);
}
