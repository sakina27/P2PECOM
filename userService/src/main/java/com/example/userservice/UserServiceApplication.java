package com.example.userservice;

import com.example.userservice.entity.Product;
import com.example.userservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    // Insert dummy products at startup (but don't crash if table doesn't exist yet)
    @Bean
    CommandLineRunner initProducts(ProductRepository productRepository) {
        return args -> {
            try {
                Long count = productRepository.count();
                if (count == 0) {
                    // Generic marketplace products (no sellerId)
                    productRepository.save(Product.builder()
                            .name("Wireless Headphones")
                            .description("Noise-cancelling over-ear headphones")
                            .price(new BigDecimal("3999.00"))
                            .sellerId(null)
                            .build());
                    productRepository.save(Product.builder()
                            .name("Gaming Mouse")
                            .description("Ergonomic RGB gaming mouse")
                            .price(new BigDecimal("1499.00"))
                            .sellerId(null)
                            .build());
                    productRepository.save(Product.builder()
                            .name("Mechanical Keyboard")
                            .description("Blue switch mechanical keyboard")
                            .price(new BigDecimal("2999.00"))
                            .sellerId(null)
                            .build());
                    productRepository.save(Product.builder()
                            .name("4K Smart TV")
                            .description("55'' Ultra HD LED Smart TV with HDR")
                            .price(new BigDecimal("42999.00"))
                            .sellerId(null)
                            .build());
                    productRepository.save(Product.builder()
                            .name("Smartphone")
                            .description("6.5'' AMOLED display, 8GB RAM, 128GB storage")
                            .price(new BigDecimal("25999.00"))
                            .sellerId(null)
                            .build());
                    System.out.println("Dummy marketplace products inserted.");
                }
            } catch (Exception e) {
                System.out.println("Skipping dummy product init (will try next run): " + e.getMessage());
            }
        };
    }

}
