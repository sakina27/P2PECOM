package com.example.userservice.otp;

import com.example.userservice.entity.OtpCode;
import com.example.userservice.entity.User;
import com.example.userservice.repository.OtpCodeRepository;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final UserRepository userRepository;
    private final Map<String, OtpSender> otpSenders; // injected by Spring

    public void generateAndSendOtp(Long userId, String purpose, String channel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code = String.valueOf(100000 + new Random().nextInt(900000));

        OtpCode otp = OtpCode.builder()
                .user(user)
                .purpose(purpose)
                .code(code)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .consumed(false)
                .build();

        otpCodeRepository.save(otp);

        String beanName = channel + "OtpSender"; // "emailOtpSender" or "smsOtpSender"
        OtpSender sender = otpSenders.get(beanName);
        if (sender == null) {
            throw new RuntimeException("Unsupported OTP channel: " + channel);
        }
        sender.sendOtp(user, code, purpose);
    }

    public boolean verifyOtp(Long userId, String purpose, String code) {
        OtpCode otp = otpCodeRepository
                .findTopByUserIdAndPurposeAndConsumedFalseOrderByExpiresAtDesc(userId, purpose)
                .orElse(null);

        if (otp == null) return false;
        if (otp.getExpiresAt().isBefore(Instant.now())) return false;
        if (!otp.getCode().equals(code)) return false;

        otp.setConsumed(true);
        otpCodeRepository.save(otp);

        return true;
    }
}
