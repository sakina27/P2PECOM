package com.example.userservice.dto;

import com.example.userservice.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {

    private String email;
    private String fullName;
    private String phone;
    private boolean hasSellerProfile;
    private String activeRole;

    public static UserProfileDto from(User user, boolean hasSellerProfile) {
        return UserProfileDto.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .hasSellerProfile(hasSellerProfile)
                .activeRole(user.getActiveRole().name())
                .build();
    }
}
