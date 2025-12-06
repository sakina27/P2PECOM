package com.example.userservice.repository;

import com.example.userservice.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findTopByUserIdAndPurposeAndConsumedFalseOrderByExpiresAtDesc(
            Long userId, String purpose
    );
}
