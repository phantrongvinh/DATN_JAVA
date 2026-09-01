package com.datn.project.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datn.project.entity.Order;

public interface IOrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // query fetch đơn hàng chi tiết
    @Query("""
                SELECT o FROM Order o
                LEFT JOIN FETCH o.orderDetails od
                LEFT JOIN FETCH od.productVariant pv
                LEFT JOIN FETCH pv.product
                LEFT JOIN FETCH o.user
                LEFT JOIN FETCH o.paymentMethod
                LEFT JOIN FETCH o.voucher
                LEFT JOIN FETCH o.timePromotion
                WHERE o.id = :id
            """)
    Optional<Order> findByIdWithDetails(@Param("id") Integer id);

    // query thống kê danh thu theo năm từng tháng
    @Query("""
                SELECT MONTH(o.createdAt), YEAR(o.createdAt),
                       COALESCE(SUM(o.finalPrice), 0),
                       COUNT(o.id)
                FROM Order o
                WHERE o.status IN ('CONFIRMED', 'DELIVERED')
                AND o.paymentStatus = 'PAID'
                AND YEAR(o.createdAt) = :year
                GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
                ORDER BY MONTH(o.createdAt)
            """)
    List<Object[]> findMonthlyRevenue(@Param("year") Integer year);

    // query thống kê só lượng order theo tháng theo trạng thái
    @Query("""
                SELECT MONTH(o.createdAt), YEAR(o.createdAt),
                       COUNT(o.id),
                       SUM(CASE WHEN o.status = 'CONFIRMED'  THEN 1 ELSE 0 END),
                       SUM(CASE WHEN o.status = 'SHIPPING'   THEN 1 ELSE 0 END),
                       SUM(CASE WHEN o.status = 'DELIVERED'  THEN 1 ELSE 0 END),
                       SUM(CASE WHEN o.status = 'CANCELLED'  THEN 1 ELSE 0 END)
                FROM Order o
                WHERE YEAR(o.createdAt) = :year
                GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
                ORDER BY MONTH(o.createdAt)
            """)
    List<Object[]> findMonthlyOrders(@Param("year") Integer year);

    // query thống kê doanh thu theo tháng
    @Query("""
                SELECT COALESCE(SUM(o.finalPrice), 0)
                FROM Order o
                WHERE o.status IN ('CONFIRMED', 'DELIVERED')
                AND o.paymentStatus = 'PAID'
                AND MONTH(o.createdAt) = :month
                AND YEAR(o.createdAt) = :year
            """)
    BigDecimal findRevenueByMonth(@Param("month") Integer month, @Param("year") Integer year);

    // query thống kê số lượng đơn hàng theo tháng
    @Query("""
                SELECT COUNT(o.id)
                FROM Order o
                WHERE MONTH(o.createdAt) = :month
                AND YEAR(o.createdAt) = :year
            """)
    Integer findOrderCountByMonth(@Param("month") Integer month, @Param("year") Integer year);

    // query thống kê doanh thu hôm nay
    @Query("""
                SELECT COALESCE(SUM(o.finalPrice), 0)
                FROM Order o
                WHERE FUNCTION('DATE', o.createdAt) = CURRENT_DATE
                AND o.status <> 'CANCELLED'
            """)
    BigDecimal findRevenueToday();

    // query thống kê đơn hàng theo trạng thái
    @Query("""
                SELECT o.status, COUNT(o.id)
                FROM Order o
                WHERE DATE(o.createdAt) = CURRENT_DATE
                GROUP BY o.status
            """)
    List<Object[]> findOrderStatusToday();

    // query thống kê 5 đơn hàng mới nhất hôm nay
    @Query("""
                SELECT o FROM Order o
                LEFT JOIN FETCH o.user
                LEFT JOIN FETCH o.paymentMethod
                WHERE DATE(o.createdAt) = CURRENT_DATE
                ORDER BY o.createdAt DESC
            """)
    List<Order> findTop5Today(Pageable pageable);

    // query thống kê số đơn đã hoàn thành theo user id
    @Query("""
                SELECT o.user.id, o.id, o.finalPrice, o.status, o.createdAt
                FROM Order o
                WHERE o.user.id IN :userIds
                AND o.status = 'DELIVERED'
                ORDER BY o.createdAt DESC
            """)
    List<Object[]> findDeliveredOrdersByUserIds(@Param("userIds") List<Integer> userIds);

    // query thống kê doanh thu theo khoảng thời gian
    @Query("""
                SELECT COALESCE(SUM(o.finalPrice), 0)
                FROM Order o
                WHERE o.createdAt >= :start AND o.createdAt < :end
                AND o.status <> 'CANCELLED'
            """)
    BigDecimal findRevenueByRange(LocalDateTime start, LocalDateTime end);

    // query thống kê số đơn theo khoảng thời gian
    @Query("""
                SELECT COUNT(o)
                FROM Order o
                WHERE o.createdAt >= :start AND o.createdAt < :end
                AND o.status <> 'CANCELLED'
            """)
    Integer findOrderCountByRange(LocalDateTime start, LocalDateTime end);

    // query đếm số đơn hàng không tính đơn đã hủy
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.status <> 'CANCELLED'")
    Integer countByUserIdExcludingCancelled(Integer userId);

    Optional<Order> findByGhnOrderCode(String ghnOrderCode);
}
