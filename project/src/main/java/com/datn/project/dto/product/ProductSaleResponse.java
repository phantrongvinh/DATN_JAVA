package com.datn.project.dto.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaleResponse {
    private int id;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private String name;
    private String image;
    private BigDecimal promotionDiscount;
}
