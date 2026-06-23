package com.datn.project.dto.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private int id;
    private String name;
    private int brandID;
    private int categoryID;
    private BigDecimal price;
}
