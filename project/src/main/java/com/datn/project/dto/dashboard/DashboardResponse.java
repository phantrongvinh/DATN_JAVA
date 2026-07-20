package com.datn.project.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal revenueToday;

    private BigDecimal revenueInRange;
    private BigDecimal revenuePreviousRange;
    private double revenueGrowthPercent;

    private Integer ordersInRange;
    private Integer ordersPreviousRange;
    private double ordersGrowthPercent;

    private Integer newCustomersInRange;
    private Integer newCustomersPreviousRange;
    private double customersGrowthPercent;

    private List<MonthlyRevenueDTO> monthlyRevenue;
    private List<MonthlyOrderDTO> monthlyOrders;
    private List<CategoryStatsDTO> categoryStats;
    private List<OrderStatusStatsDTO> orderStatusToday;
    private List<OrderSummaryResponse> recentOrdersToday;

    private List<TopProductDTO> topSellingProducts;
    private List<TopProductDTO> topInteractedProducts;
}