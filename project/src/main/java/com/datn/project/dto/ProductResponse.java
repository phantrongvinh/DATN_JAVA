package com.datn.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private int id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private LocalDateTime createdAt;
    private String category;
    private String brand;
    private String targetAudience;
    private boolean isAccessory;
    private String img;
    private List<ProductVariantResponse> productVariant;
}
