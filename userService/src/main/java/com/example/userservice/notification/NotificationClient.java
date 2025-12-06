package com.example.userservice.notification;

import com.example.userservice.dto.NotificationRequest;
import com.example.userservice.dto.NotificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class NotificationClient {

    @Value("${notification.base-url:http://localhost:8081}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Template-based OTP (preferred)
    public NotificationResponse sendOtpSms(String to, String otp) {
        NotificationRequest payload = new NotificationRequest();
        payload.setChannel("SMS");
        payload.setRecipient(to);
        payload.setTemplateId("OTP_VERIFY");          // must match notification module template
        payload.setTemplateParams(Map.of("otp", otp));

        return post(payload);
    }

    // Free-text SMS fallback
    public NotificationResponse sendSms(String to, String body) {
        NotificationRequest payload = new NotificationRequest();
        payload.setChannel("SMS");
        payload.setRecipient(to);
        payload.setTemplateId("FREE_TEXT");          // notification module must accept this or change accordingly
        payload.setTemplateParams(Map.of("body", body));

        return post(payload);
    }

    // Template-based Email (preferred)
    public NotificationResponse sendEmail(String to, String subject, String body) {
        NotificationRequest payload = new NotificationRequest();
        payload.setChannel("EMAIL");
        payload.setRecipient(to);
        payload.setTemplateId("OTP_VERIFY_EMAIL");   // or a template your notification module understands
        payload.setTemplateParams(Map.of(
                "subject", subject,
                "body", body
        ));

        return post(payload);
    }

    // Generic POST helper that calls the notification module endpoint.
    private NotificationResponse post(NotificationRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<NotificationRequest> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<NotificationResponse> response = restTemplate.postForEntity(
                    baseUrl + "/api/v1/notifications/send",
                    entity,
                    NotificationResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                System.out.println("Notification service returned non-2xx: " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("Notification service call failed: " + e.getMessage());
            return null;
        }
    }
}
