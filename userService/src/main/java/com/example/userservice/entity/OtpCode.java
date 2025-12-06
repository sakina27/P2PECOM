package com.example.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "otp_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String purpose;     // e.g. SWITCH_TO_SELLER
    private String code;
    private Instant expiresAt;
    private boolean consumed;
}
