package com.datn.project.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datn.project.entity.Promotion;

public interface IPromotionRepository  extends JpaRepository<Promotion,Integer>{
     @Query("""
        SELECT p FROM Promotion p
        JOIN p.products pr
        WHERE pr.id = :productId
        AND :now BETWEEN p.startAt AND p.endAt
        ORDER BY p.discountValue DESC
    """)
    Optional<Promotion> findActiveByProductId(
        @Param("productId") Integer productId,
        @Param("now") LocalDateTime now
    );
}
