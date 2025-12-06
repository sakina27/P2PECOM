package com.example.userservice.otp;

import com.example.userservice.entity.User;

public interface OtpSender {
    void sendOtp(User user, String code, String purpose);
}
