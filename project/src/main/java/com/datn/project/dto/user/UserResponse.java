package com.datn.project.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String provider;
    private boolean isActived;
    private LocalDateTime createdAt;

    // Thống kê đơn hàng
    private Integer totalOrders;
    private Integer completedOrders;
    private BigDecimal totalSpending;
    private List<OrderSummaryResponse> deliveredOrders;
}
