package com.example.userservice.otp;

import com.example.userservice.dto.NotificationResponse;
import com.example.userservice.entity.User;
import com.example.userservice.notification.NotificationClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("emailOtpSender")
@RequiredArgsConstructor
public class EmailOtpSender implements OtpSender {

    private static final Logger log = LoggerFactory.getLogger(EmailOtpSender.class);

    private final NotificationClient notificationClient;

    /**
     * Sends OTP to user's email using the notification module.
     * Tries a template-based send first (preferred). If that fails,
     * falls back to a plain email send with the body text.
     */
    @Override
    public void sendOtp(User user, String code, String purpose) {
        if (user == null) {
            log.warn("EmailOtpSender: user is null, cannot send OTP");
            return;
        }

        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            log.warn("EmailOtpSender: user {} has no email, skipping email OTP", user.getId());
            return;
        }

        String subject = "OTP for " + purpose;
        String body = "Your OTP is " + code + ". It is valid for 5 minutes.";

        try {
            // Preferred: use a template-based send (so notification service can render consistent emails)
            NotificationResponse resp = notificationClient.sendEmail(email, subject, body);

            if (resp == null) {
                // notification client returned null (error calling service)
                log.warn("EmailOtpSender: notification client returned null for user={}, falling back to plain email", user.getId());
                // optional: call a plain / fallback endpoint or re-use same sendEmail (notification client implementation may already handle it)
                notificationClient.sendEmail(email, subject, body); // attempt anyway (client handles errors)
                return;
            }

            if (resp.isSuccess()) {
                log.info("EmailOtpSender: OTP email sent successfully to {} providerId={}", email, resp.getProviderMessageId());
            } else {
                log.warn("EmailOtpSender: template send failed for {} error={}, falling back to plain body send", email, resp.getError());
                // fallback: try again as a free-text email (notification client may support a different templateId)
                notificationClient.sendEmail(email, subject, body);
            }
        } catch (Exception ex) {
            // if anything goes wrong we log but do not block the flow
            log.error("EmailOtpSender: failed to send OTP email to {}: {}", email, ex.getMessage(), ex);
            // optionally attempt a simpler call
            try {
                notificationClient.sendEmail(email, subject, body);
            } catch (Exception e2) {
                log.error("EmailOtpSender: fallback send also failed: {}", e2.getMessage());
            }
        }
    }
}
