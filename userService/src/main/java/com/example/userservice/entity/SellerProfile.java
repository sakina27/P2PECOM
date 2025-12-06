package com.example.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "seller_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String shopName;
    private String gstNumber;
    private String address;

    private Instant createdAt = Instant.now();
}
