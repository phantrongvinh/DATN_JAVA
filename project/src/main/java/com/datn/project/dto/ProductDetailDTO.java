package com.datn.project.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal basePrice;

    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private String brandLogo;
    private Integer targetAudienceId;
    private String targetAudienceName;

    private List<ProductImageDTO> images;
    private List<ProductVariantDTO> variants;

}



