package com.datn.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.datn.project.dto.dashboard.CategoryStatsDTO;
import com.datn.project.dto.dashboard.DashboardResponse;
import com.datn.project.dto.dashboard.MonthlyOrderDTO;
import com.datn.project.dto.dashboard.MonthlyRevenueDTO;
import com.datn.project.dto.dashboard.OrderStatusStatsDTO;
import com.datn.project.dto.dashboard.OrderSummaryResponse;
import com.datn.project.entity.Order;
import com.datn.project.repository.IOrderRepository;
import com.datn.project.repository.IProductRepository;
import com.datn.project.repository.IUserRepository;

@Service
public class DashboardService implements IDashboardService {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public DashboardResponse getDashboard(Integer month, Integer year) {
        if (month == null)
            month = LocalDate.now().getMonthValue();
        if (year == null)
            year = LocalDate.now().getYear();

        final int currentYear = year;

        // ─── Doanh thu tháng hiện tại và tháng trước ─
        BigDecimal revenueThis = orderRepository.findRevenueByMonth(month, year);
        Integer prevMonth = month == 1 ? 12 : month - 1;
        Integer prevYear = month == 1 ? year - 1 : year;
        BigDecimal revenueLast = orderRepository.findRevenueByMonth(prevMonth, prevYear);
        double revenueGrowth = calcGrowth(revenueThis, revenueLast);

        // ─── Đơn hàng tháng ──────────────────────────
        Integer ordersThis = orderRepository.findOrderCountByMonth(month, year);
        Integer ordersLast = orderRepository.findOrderCountByMonth(prevMonth, prevYear);
        double ordersGrowth = calcGrowth(
                BigDecimal.valueOf(ordersThis),
                BigDecimal.valueOf(ordersLast));

        // ─── Khách hàng mới ───────────────────────────
        Integer customersThis = userRepository.findNewCustomersByMonth(month, year);
        Integer customersLast = userRepository.findNewCustomersByMonth(prevMonth, prevYear);
        double customersGrowth = calcGrowth(
                BigDecimal.valueOf(customersThis),
                BigDecimal.valueOf(customersLast));

        // ─── Monthly revenue chart ────────────────────
        List<Object[]> monthlyRaw = orderRepository.findMonthlyRevenue(currentYear);
        Map<Integer, Object[]> monthlyMap = monthlyRaw.stream()
                .collect(Collectors.toMap(r -> (Integer) r[0], r -> r));

        List<MonthlyRevenueDTO> monthlyRevenue = IntStream.rangeClosed(1, 12)
                .mapToObj(m -> {
                    Object[] row = monthlyMap.get(m);
                    return MonthlyRevenueDTO.builder()
                            .month(m)
                            .year(currentYear)
                            .revenue(row != null ? (BigDecimal) row[2] : BigDecimal.ZERO)
                            .orders(row != null ? ((Long) row[3]).intValue() : 0)
                            .build();
                })
                .toList();

        // ─── Monthly orders chart ─────────────────────
        List<Object[]> ordersRaw = orderRepository.findMonthlyOrders(currentYear);
        Map<Integer, Object[]> ordersMap = ordersRaw.stream()
                .collect(Collectors.toMap(r -> (Integer) r[0], r -> r));

        List<MonthlyOrderDTO> monthlyOrders = IntStream.rangeClosed(1, 12)
                .mapToObj(m -> {
                    Object[] row = ordersMap.get(m);
                    return MonthlyOrderDTO.builder()
                            .month(m)
                            .year(currentYear)
                            .total(row != null ? ((Long) row[2]).intValue() : 0)
                            .confirmed(row != null ? ((Long) row[3]).intValue() : 0)
                            .shipping(row != null ? ((Long) row[4]).intValue() : 0)
                            .delivered(row != null ? ((Long) row[5]).intValue() : 0)
                            .cancelled(row != null ? ((Long) row[6]).intValue() : 0)
                            .build();
                })
                .toList();

        // ─── Category stats ───────────────────────────
        List<Object[]> categoryRaw = productRepository.findCategoryStats();
        long totalProducts = categoryRaw.stream()
                .mapToLong(r -> (Long) r[1]).sum();

        List<CategoryStatsDTO> categoryStats = categoryRaw.stream()
                .map(r -> CategoryStatsDTO.builder()
                        .categoryName((String) r[0])
                        .productCount((Long) r[1])
                        .percentage(totalProducts > 0
                                ? Math.round(((Long) r[1] * 100.0 / totalProducts) * 10.0) / 10.0
                                : 0.0)
                        .build())
                .toList();

        // ─── Order status today ───────────────────────
        List<Object[]> statusRaw = orderRepository.findOrderStatusToday();
        List<OrderStatusStatsDTO> orderStatusToday = statusRaw.stream()
                .map(r -> OrderStatusStatsDTO.builder()
                        .status(r[0].toString())
                        .count((Long) r[1])
                        .build())
                .toList();

        // ─── Top 5 orders today ───────────────────────
        List<Order> todayOrders = orderRepository.findTop5Today(
                PageRequest.of(0, 5));
        List<OrderSummaryResponse> recentOrdersToday = todayOrders.stream()
                .map(o -> OrderSummaryResponse.builder()
                        .id(o.getId())
                        .finalPrice(o.getFinalPrice())
                        .status(o.getStatus().name())
                        .createdAt(o.getCreatedAt())
                        .build())
                .toList();

        return DashboardResponse.builder()
                .revenueThisMonth(revenueThis)
                .revenueLastMonth(revenueLast)
                .revenueGrowthPercent(revenueGrowth)
                .ordersThisMonth(ordersThis)
                .ordersLastMonth(ordersLast)
                .ordersGrowthPercent(ordersGrowth)
                .newCustomersThisMonth(customersThis)
                .newCustomersLastMonth(customersLast)
                .customersGrowthPercent(customersGrowth)
                .monthlyRevenue(monthlyRevenue)
                .monthlyOrders(monthlyOrders)
                .categoryStats(categoryStats)
                .orderStatusToday(orderStatusToday)
                .recentOrdersToday(recentOrdersToday)
                .build();
    }

    @Override
    public double calcGrowth(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return Math.round(
                current.subtract(previous)
                        .divide(previous, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue() * 10)
                / 10.0;
    }
}