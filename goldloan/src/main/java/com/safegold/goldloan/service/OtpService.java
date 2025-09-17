package com.safegold.goldloan.service;

import com.safegold.goldloan.model.OtpVerification;
import com.safegold.goldloan.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepo;

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void saveOtp(String mobileNumber, String otp) {
        Optional<OtpVerification> recordOpt = otpRepo.findByMobileNumber(mobileNumber);
        OtpVerification record = recordOpt.orElse(new OtpVerification());
        record.setMobileNumber(mobileNumber);
        record.setOtpCode(otp);
        record.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        record.setVerified(false);
        otpRepo.save(record);
    }

    public boolean validateOtp(String mobileNumber, String enteredOtp) {
        Optional<OtpVerification> recordOpt = otpRepo.findByMobileNumber(mobileNumber);
        if (recordOpt.isPresent()) {
            OtpVerification record = recordOpt.get();
            if (record.getOtpCode().equals(enteredOtp)
                    && record.getExpiryTime().isAfter(LocalDateTime.now())) {
                record.setVerified(true);
                otpRepo.save(record);
                return true;
            }
        }
        return false;
    }
}
