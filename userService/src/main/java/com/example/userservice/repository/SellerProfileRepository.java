package com.example.userservice.repository;

import com.example.userservice.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    Optional<SellerProfile> findByUserId(Long userId);
}
