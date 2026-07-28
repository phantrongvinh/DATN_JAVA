package com.datn.project.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderSummaryResponse {
    private Integer id;
    private BigDecimal finalPrice;
    private String status;
    private LocalDateTime createdAt;
}
