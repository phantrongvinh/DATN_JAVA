package com.datn.project.dto.chatbox;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProposedItem {
    private Integer productVariantId;
    private Integer productId;
    private String productName;
    private String color;
    private String sizeName;
    private Integer quantity;
    private Integer stock;
    private BigDecimal price;
    private String image;
}