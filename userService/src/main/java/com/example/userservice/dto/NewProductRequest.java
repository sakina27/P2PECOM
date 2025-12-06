package com.example.userservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
}
