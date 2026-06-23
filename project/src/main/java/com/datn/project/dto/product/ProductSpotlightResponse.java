package com.datn.project.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpotlightResponse {
    
    private int id;
    private String name;
    private String img;
}
