package com.datn.project.dto.dashboard;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyRevenueDTO {
    private Integer month;
    private Integer year;
    private BigDecimal revenue;
    private Integer orders;
}