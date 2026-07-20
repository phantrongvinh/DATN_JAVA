package com.datn.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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
import com.datn.project.dto.dashboard.TopProductDTO;
import com.datn.project.entity.Order;
import com.datn.project.repository.IOrderDetailRepository;
import com.datn.project.repository.IOrderRepository;
import com.datn.project.repository.IProductImageRepository;
import com.datn.project.repository.IProductRepository;
import com.datn.project.repository.IProductReviewRepository;
import com.datn.project.repository.IUserRepository;
import com.datn.project.repository.IWishlistRepository;

@Service
public class DashboardService implements IDashboardService {

        @Autowired
        private IOrderRepository orderRepository;

        @Autowired
        private IProductRepository productRepository;

        @Autowired
        private IUserRepository userRepository;

        @Autowired
        private IProductImageRepository productImageRepository;

        @Autowired
        private IOrderDetailRepository orderDetailRepository;

        @Autowired
        private IWishlistRepository wishlistRepository;

        @Autowired
        private IProductReviewRepository productReviewRepository;

        @Override
        public DashboardResponse getDashboard(LocalDate start, LocalDate end, Integer chartYear) {
                if (start == null)
                        start = LocalDate.now();
                if (end == null)
                        end = LocalDate.now();

                LocalDateTime rangeStart = start.atStartOfDay();
                LocalDateTime rangeEnd = end.plusDays(1).atStartOfDay();

                long days = ChronoUnit.DAYS.between(start, end) + 1;
                LocalDate prevEnd = start.minusDays(1);
                LocalDate prevStart = prevEnd.minusDays(days - 1);
                LocalDateTime prevRangeStart = prevStart.atStartOfDay();
                LocalDateTime prevRangeEnd = prevEnd.plusDays(1).atStartOfDay();

                // ─── Doanh thu / đơn hàng / khách hàng mới theo range ─
                BigDecimal revenueCurrent = orderRepository.findRevenueByRange(rangeStart, rangeEnd);
                BigDecimal revenuePrevious = orderRepository.findRevenueByRange(prevRangeStart, prevRangeEnd);
                double revenueGrowth = calcGrowth(revenueCurrent, revenuePrevious);

                Integer ordersCurrent = orderRepository.findOrderCountByRange(rangeStart, rangeEnd);
                Integer ordersPrevious = orderRepository.findOrderCountByRange(prevRangeStart, prevRangeEnd);
                double ordersGrowth = calcGrowth(BigDecimal.valueOf(ordersCurrent), BigDecimal.valueOf(ordersPrevious));

                Integer customersCurrent = userRepository.findNewCustomersByRange(rangeStart, rangeEnd);
                Integer customersPrevious = userRepository.findNewCustomersByRange(prevRangeStart, prevRangeEnd);
                double customersGrowth = calcGrowth(BigDecimal.valueOf(customersCurrent),
                                BigDecimal.valueOf(customersPrevious));

                // ─── Doanh thu hôm nay — cố định, không phụ thuộc range ─
                LocalDateTime todayStart = LocalDate.now().atStartOfDay();
                LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
                BigDecimal revenueToday = orderRepository.findRevenueByRange(todayStart, todayEnd);

                // ─── Biểu đồ theo năm ───────────────────────
                int year = chartYear != null ? chartYear : LocalDate.now().getYear();
                final int currentYear = year;

                Map<Integer, Object[]> monthlyMap = orderRepository.findMonthlyRevenue(currentYear).stream()
                                .collect(Collectors.toMap(r -> (Integer) r[0], r -> r));
                List<MonthlyRevenueDTO> monthlyRevenue = IntStream.rangeClosed(1, 12)
                                .mapToObj(m -> {
                                        Object[] row = monthlyMap.get(m);
                                        return MonthlyRevenueDTO.builder()
                                                        .month(m).year(currentYear)
                                                        .revenue(row != null ? (BigDecimal) row[2] : BigDecimal.ZERO)
                                                        .orders(row != null ? ((Long) row[3]).intValue() : 0)
                                                        .build();
                                }).toList();

                Map<Integer, Object[]> ordersMap = orderRepository.findMonthlyOrders(currentYear).stream()
                                .collect(Collectors.toMap(r -> (Integer) r[0], r -> r));
                List<MonthlyOrderDTO> monthlyOrders = IntStream.rangeClosed(1, 12)
                                .mapToObj(m -> {
                                        Object[] row = ordersMap.get(m);
                                        return MonthlyOrderDTO.builder()
                                                        .month(m).year(currentYear)
                                                        .total(row != null ? ((Long) row[2]).intValue() : 0)
                                                        .confirmed(row != null ? ((Long) row[3]).intValue() : 0)
                                                        .shipping(row != null ? ((Long) row[4]).intValue() : 0)
                                                        .delivered(row != null ? ((Long) row[5]).intValue() : 0)
                                                        .cancelled(row != null ? ((Long) row[6]).intValue() : 0)
                                                        .build();
                                }).toList();

                // ─── Category stats ─────────────────────────
                List<Object[]> categoryRaw = productRepository.findCategoryStats();
                long totalProducts = categoryRaw.stream().mapToLong(r -> (Long) r[1]).sum();
                List<CategoryStatsDTO> categoryStats = categoryRaw.stream()
                                .map(r -> CategoryStatsDTO.builder()
                                                .categoryName((String) r[0])
                                                .productCount((Long) r[1])
                                                .percentage(totalProducts > 0
                                                                ? Math.round(((Long) r[1] * 100.0 / totalProducts)
                                                                                * 10.0) / 10.0
                                                                : 0.0)
                                                .build())
                                .toList();

                // ─── Trạng thái + top 5 đơn hôm nay ──────────
                List<OrderStatusStatsDTO> orderStatusToday = orderRepository.findOrderStatusToday().stream()
                                .map(r -> OrderStatusStatsDTO.builder().status(r[0].toString()).count((Long) r[1])
                                                .build())
                                .toList();

                List<Order> todayOrders = orderRepository.findTop5Today(PageRequest.of(0, 5));
                List<OrderSummaryResponse> recentOrdersToday = todayOrders.stream()
                                .map(o -> OrderSummaryResponse.builder()
                                                .id(o.getId()).finalPrice(o.getFinalPrice())
                                                .status(o.getStatus().name()).createdAt(o.getCreatedAt())
                                                .build())
                                .toList();

                // ─── Top bán chạy ────────────────────────────
                List<Object[]> topSellingRaw = orderDetailRepository.findTopSellingProducts(
                                rangeStart, rangeEnd, PageRequest.of(0, 5));
                List<Integer> topSellingIds = topSellingRaw.stream().map(r -> (Integer) r[0]).toList();
                Map<Integer, String> topSellingImages = resolvePrimaryImages(topSellingIds);

                List<TopProductDTO> topSellingProducts = topSellingRaw.stream()
                                .map(r -> {
                                        Integer productId = (Integer) r[0];
                                        return TopProductDTO.builder()
                                                        .productId(productId)
                                                        .productName((String) r[1])
                                                        .image(topSellingImages.get(productId))
                                                        .value((Long) r[2])
                                                        .build();
                                }).toList();

                // ─── Top tương tác (wishlist + review) ───────
                Map<Integer, Long> interactionMap = new HashMap<>();
                wishlistRepository.countWishlistByProductInRange(rangeStart, rangeEnd)
                                .forEach(r -> interactionMap.merge((Integer) r[0], (Long) r[1], Long::sum));
                productReviewRepository.countReviewsByProductInRange(rangeStart, rangeEnd)
                                .forEach(r -> interactionMap.merge((Integer) r[0], (Long) r[1], Long::sum));

                List<Integer> topInteractedIds = interactionMap.entrySet().stream()
                                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                                .limit(5).map(Map.Entry::getKey).toList();
                Map<Integer, String> topInteractedImages = resolvePrimaryImages(topInteractedIds);

                List<TopProductDTO> topInteractedProducts = topInteractedIds.stream()
                                .map(productId -> {
                                        var p = productRepository.findById(productId).orElse(null);
                                        return TopProductDTO.builder()
                                                        .productId(productId)
                                                        .productName(p != null ? p.getName() : "N/A")
                                                        .image(topInteractedImages.get(productId))
                                                        .value(interactionMap.get(productId))
                                                        .build();
                                }).toList();

                return DashboardResponse.builder()
                                .revenueToday(revenueToday)
                                .revenueInRange(revenueCurrent)
                                .revenuePreviousRange(revenuePrevious)
                                .revenueGrowthPercent(revenueGrowth)
                                .ordersInRange(ordersCurrent)
                                .ordersPreviousRange(ordersPrevious)
                                .ordersGrowthPercent(ordersGrowth)
                                .newCustomersInRange(customersCurrent)
                                .newCustomersPreviousRange(customersPrevious)
                                .customersGrowthPercent(customersGrowth)
                                .monthlyRevenue(monthlyRevenue)
                                .monthlyOrders(monthlyOrders)
                                .categoryStats(categoryStats)
                                .orderStatusToday(orderStatusToday)
                                .recentOrdersToday(recentOrdersToday)
                                .topSellingProducts(topSellingProducts)
                                .topInteractedProducts(topInteractedProducts)
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

        private Map<Integer, String> resolvePrimaryImages(List<Integer> productIds) {
                if (productIds.isEmpty())
                        return Map.of();
                Map<Integer, String> result = new HashMap<>();
                for (Object[] row : productImageRepository.findImagesByProductIds(productIds)) {
                        result.putIfAbsent((Integer) row[0], (String) row[1]);
                }
                return result;
        }
}