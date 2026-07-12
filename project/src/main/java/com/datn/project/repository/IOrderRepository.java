package com.datn.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datn.project.entity.Order;

public interface IOrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

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

    @Query("""
                SELECT o.user.id, o.id, o.finalPrice, o.status, o.createdAt
                FROM Order o
                WHERE o.user.id IN :userIds
                AND o.status = 'DELIVERED'
                ORDER BY o.createdAt DESC
            """)
    List<Object[]> findDeliveredOrdersByUserIds(@Param("userIds") List<Integer> userIds);
}
