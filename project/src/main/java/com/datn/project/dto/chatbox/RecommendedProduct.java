package com.datn.project.dto.chatbox;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RecommendedProduct {
    private Integer productId;
    private Integer productVariantId;
    private String name;
    private String color;
    private String sizeName;
    private BigDecimal price;
    private String image;
}