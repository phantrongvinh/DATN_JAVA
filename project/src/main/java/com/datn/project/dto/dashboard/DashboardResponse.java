package com.datn.project.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal revenueThisMonth;
    private BigDecimal revenueLastMonth;
    private Double revenueGrowthPercent;     

    private Integer ordersThisMonth;
    private Integer ordersLastMonth;
    private Double ordersGrowthPercent;

    private Integer newCustomersThisMonth;
    private Integer newCustomersLastMonth;
    private Double customersGrowthPercent;

    private List<MonthlyRevenueDTO> monthlyRevenue;

    private List<MonthlyOrderDTO> monthlyOrders;

    private List<CategoryStatsDTO> categoryStats;

    private List<OrderStatusStatsDTO> orderStatusToday;

    private List<OrderSummaryResponse> recentOrdersToday;
}