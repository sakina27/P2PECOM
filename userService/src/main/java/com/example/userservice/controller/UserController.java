package com.example.userservice.controller;

import com.example.userservice.dto.*;
import com.example.userservice.entity.Product;
import com.example.userservice.entity.SellerProfile;
import com.example.userservice.entity.User;
import com.example.userservice.otp.OtpService;
import com.example.userservice.repository.ProductRepository;
import com.example.userservice.repository.SellerProfileRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtService;
import com.example.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.userservice.dto.NewProductRequest;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final ProductRepository productRepository;
    private final OtpService otpService;
    private final JwtService jwtService;

    // --- Auth ---

    @PostMapping("/auth/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // --- Current user profile ---

    @GetMapping("/users/me")
    public UserProfileDto me(@AuthenticationPrincipal User user) {
        SellerProfile sellerProfile =
                sellerProfileRepository.findByUserId(user.getId()).orElse(null);

        boolean hasSellerProfile = sellerProfile != null && user.isHasSellerProfile();
        return UserProfileDto.from(user, hasSellerProfile);
    }

    // --- Register as seller ---

    @PostMapping("/seller/register")
    public ResponseEntity<?> registerSeller(@AuthenticationPrincipal User user,
                                            @RequestBody SellerRegisterRequest request) {
        if (user.isHasSellerProfile()) {
            return ResponseEntity.badRequest().body("Already a seller");
        }

        SellerProfile profile = SellerProfile.builder()
                .user(user)
                .shopName(request.getShopName())
                .gstNumber(request.getGstNumber())
                .address(request.getAddress())
                .build();

        sellerProfileRepository.save(profile);

        user.setHasSellerProfile(true);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // --- Request OTP to switch to seller ---

    @PostMapping("/users/switch-role/request-otp")
    public ResponseEntity<?> requestSwitchRoleOtp(
            @AuthenticationPrincipal User user,
            @RequestParam String channel   // no defaultValue - required now
    ) {
        if (!user.isHasSellerProfile()) {
            return ResponseEntity.badRequest().body("User is not a seller");
        }

        if (channel == null || (!channel.equalsIgnoreCase("email") && !channel.equalsIgnoreCase("sms"))) {
            return ResponseEntity.badRequest().body("Invalid channel. Allowed values: email, sms");
        }

        otpService.generateAndSendOtp(user.getId(), "SWITCH_TO_SELLER", channel.toLowerCase());
        return ResponseEntity.ok().build();
    }


    // --- Verify OTP and switch role ---

    @PostMapping("/users/switch-role/verify")
    public AuthResponse verifySwitchRoleOtp(@AuthenticationPrincipal User user,
                                            @RequestParam String code) {
        boolean ok = otpService.verifyOtp(user.getId(), "SWITCH_TO_SELLER", code);
        if (!ok) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        user.setActiveRole(User.ActiveRole.SELLER);
        userRepository.save(user);

        String newToken = jwtService.generateToken(user);
        return new AuthResponse(newToken);
    }

    // --- Buyer: dummy products ---

    @GetMapping("/products")
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    // --- Seller: dummy how-to-sell page ---

    @GetMapping("/seller/how-to-sell")
    public Map<String, String> howToSell(@AuthenticationPrincipal User user) {
        if (user.getActiveRole() != User.ActiveRole.SELLER) {
            throw new RuntimeException("You must switch to seller profile first");
        }

        return Map.of(
                "title", "How to sell on Our Marketplace",
                "content", """
                        1. Complete your seller profile.
                        2. Add product listings with good images.
                        3. Keep stock updated and ship quickly.
                        4. Respond to customer queries politely.
                        5. Maintain ratings for better visibility.
                        """
        );
    }

    // ---------- Seller: list own products ----------

    @GetMapping("/seller/products")
    public List<Product> listSellerProducts(@AuthenticationPrincipal User user) {
        if (user.getActiveRole() != User.ActiveRole.SELLER) {
            throw new RuntimeException("You must switch to seller profile first");
        }
        return productRepository.findBySellerId(user.getId());
    }

    // ---------- Seller: add a new product ----------

    @PostMapping("/seller/products")
    public Product addSellerProduct(@AuthenticationPrincipal User user,
                                    @RequestBody NewProductRequest request) {
        if (user.getActiveRole() != User.ActiveRole.SELLER) {
            throw new RuntimeException("You must switch to seller profile first");
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sellerId(user.getId())
                .build();

        return productRepository.save(product);
    }

    // ---------- Switch back to BUYER (no OTP) ----------

    @PostMapping("/users/switch-role/buyer")
    public AuthResponse switchToBuyer(@AuthenticationPrincipal User user) {
        user.setActiveRole(User.ActiveRole.BUYER);
        userRepository.save(user);

        String newToken = jwtService.generateToken(user);
        return new AuthResponse(newToken);
    }


}
