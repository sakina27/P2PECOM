package com.example.userservice.otp;

import com.example.userservice.entity.User;
import com.example.userservice.notification.NotificationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("smsOtpSender")
@RequiredArgsConstructor
public class SmsOtpSender implements OtpSender {

    private final NotificationClient notificationClient;

    @Override
    public void sendOtp(User user, String code, String purpose) {
        if (user.getPhone() == null) return;
        String body = "Your OTP for " + purpose + " is " + code;

        // Prefer template-based OTP
        var resp = notificationClient.sendOtpSms(user.getPhone(), code);
        if (resp != null && resp.isSuccess()) {
            System.out.println("OTP sent via notification service. providerId=" + resp.getProviderMessageId());
            return;
        }

        // fallback: try sendSms (free-text) so older notification modules still work
        var fallback = notificationClient.sendSms(user.getPhone(), body);
        if (fallback != null && fallback.isSuccess()) {
            System.out.println("OTP sent via fallback sendSms. providerId=" + fallback.getProviderMessageId());
        } else {
            System.out.println("Failed to send OTP via notification service (and fallback).");
        }
    }

}
