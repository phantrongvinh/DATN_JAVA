package com.datn.project.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryStatsDTO {
    private String categoryName;
    private Long productCount;
    private Double percentage;
}