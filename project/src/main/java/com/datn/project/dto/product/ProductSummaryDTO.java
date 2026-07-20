package com.datn.project.dto.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {
    private Integer id;
    private String name;
    private String image;
    private BigDecimal price;
}
