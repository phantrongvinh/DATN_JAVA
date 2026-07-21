package com.datn.project.dto.product;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AdminProductFilterDTO {
    private Integer audienceId;
    private Integer brandId;
    private Integer categoryId;
    private String search;
    private Boolean onSale;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy;
}
