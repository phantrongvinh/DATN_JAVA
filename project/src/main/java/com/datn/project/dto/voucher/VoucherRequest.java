package com.datn.project.dto.voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.datn.project.entity.DiscountType;

import lombok.Data;

@Data
public class VoucherRequest {
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private Integer quantity;
    private boolean isStackable;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
}
