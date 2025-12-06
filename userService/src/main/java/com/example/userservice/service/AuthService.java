package com.example.userservice.service;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.entity.User;
import com.example.userservice.notification.NotificationClient;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationClient notificationClient;

    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> { throw new RuntimeException("Email already in use"); });

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .activeRole(User.ActiveRole.BUYER)
                .build();

        userRepository.save(user);

        // notify via Notification microservice (SOA)
        notificationClient.sendEmail(
                user.getEmail(),
                "Welcome to the Marketplace",
                "Hi " + user.getFullName() + ", thanks for registering as a buyer!"
        );

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        // always login as BUYER
        user.setActiveRole(User.ActiveRole.BUYER);
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
