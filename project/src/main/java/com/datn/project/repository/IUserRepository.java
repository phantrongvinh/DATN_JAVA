package com.datn.project.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datn.project.entity.User;

public interface IUserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    // tìm user theo mail với tất cả role hiện có
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    // kiểm tra email tồn tại
    boolean existsByEmail(String email);

    // tìm usser theo email
    Optional<User> findByEmail(String email);

    // tổng số đơn hàng của user
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Integer countOrdersByUserId(@Param("userId") Integer userId);

    // tổng tiền đơn đã hoàn thành
    @Query("""
                SELECT COALESCE(SUM(o.finalPrice), 0)
                FROM Order o
                WHERE o.user.id = :userId
                AND o.status IN ( 'DELIVERED')
            """)
    BigDecimal sumSpendingByUserId(@Param("userId") Integer userId);

    // tổng đơn hoàn thành
    @Query("""
                SELECT COUNT(o) FROM Order o
                WHERE o.user.id = :userId
                AND o.status IN ( 'DELIVERED')
            """)
    Integer countCompletedOrdersByUserId(@Param("userId") Integer userId);

    // tổng các đơn theo trạng thái của user
    @Query("""
                SELECT u.id,
                       COUNT(o.id),
                       COUNT(CASE WHEN o.status IN ('CONFIRMED', 'DELIVERED') THEN 1 END),
                       COALESCE(SUM(CASE WHEN o.status IN ('CONFIRMED', 'DELIVERED')
                                    THEN o.finalPrice ELSE 0 END), 0)
                FROM User u
                LEFT JOIN Order o ON o.user.id = u.id
                WHERE u.id IN :userIds
                GROUP BY u.id
            """)
    List<Object[]> findOrderStatsByUserIds(@Param("userIds") List<Integer> userIds);

    // thống kê khách hàng mới trong khoảng
    @Query("""
                SELECT COUNT(u)
                FROM User u
                WHERE u.createdAt >= :start AND u.createdAt < :end
            """)
    Integer findNewCustomersByRange(LocalDateTime start, LocalDateTime end);

    // tìm khách hàng có sinh nhật vào thàng ?
    @Query("""
                SELECT u FROM User u
                JOIN u.roles r
                WHERE FUNCTION('MONTH', u.birthDay) = :month
                AND u.isActived = true
                AND r.name = 'USER'
            """)
    List<User> findBirthdayUsersByMonth(int month);
}
