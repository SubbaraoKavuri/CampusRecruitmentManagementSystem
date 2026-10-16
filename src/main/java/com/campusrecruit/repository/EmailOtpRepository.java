package com.campusrecruit.repository;

import com.campusrecruit.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, String purpose);
    void deleteAllByEmailAndPurpose(String email, String purpose);
}
