package com.example.userservice.dto;

import lombok.Data;

@Data
public class SellerRegisterRequest {
    private String shopName;
    private String gstNumber;
    private String address;
}
