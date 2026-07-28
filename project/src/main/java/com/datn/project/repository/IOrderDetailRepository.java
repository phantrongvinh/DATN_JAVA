package com.datn.project.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.datn.project.entity.OrderDetail;

public interface IOrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query("""
                SELECT pv.product.id, pv.product.name, SUM(od.quantity)
                FROM OrderDetail od
                JOIN od.productVariant pv
                WHERE od.order.createdAt >= :start AND od.order.createdAt < :end
                AND od.order.status <> 'CANCELLED'
                GROUP BY pv.product.id, pv.product.name
                ORDER BY SUM(od.quantity) DESC
            """)
    List<Object[]> findTopSellingProducts(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("""
                SELECT COUNT(od) > 0
                FROM OrderDetail od
                WHERE od.order.user.id = :userId
                AND od.productVariant.product.id = :productId
                AND od.order.status = 'DELIVERED'
            """)
    boolean existsDeliveredPurchase(Integer userId, Integer productId);
}
