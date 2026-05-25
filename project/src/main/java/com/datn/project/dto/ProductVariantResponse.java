package com.datn.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {
    
    private String color;
    private String size;
    private int stock;
    private BigDecimal price;
    private String sku;
    private LocalDateTime createdAt;
}
