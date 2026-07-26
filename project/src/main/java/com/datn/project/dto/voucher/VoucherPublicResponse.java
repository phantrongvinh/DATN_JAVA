package com.datn.project.dto.voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherPublicResponse {
    private String code;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private Integer quantity;
    private Integer usedCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isStackable;

    private boolean disabled;
    private String disabledReason;
}
