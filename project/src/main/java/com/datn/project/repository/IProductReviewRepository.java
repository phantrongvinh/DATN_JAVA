package com.datn.project.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.datn.project.entity.ProductReview;
import com.datn.project.entity.ProductReviewId;

public interface IProductReviewRepository extends JpaRepository<ProductReview, ProductReviewId> {
    @Query("""
                SELECT r.id.productId, COUNT(r)
                FROM ProductReview r
                WHERE r.createdAt >= :start AND r.createdAt < :end
                GROUP BY r.id.productId
            """)
    List<Object[]> countReviewsByProductInRange(LocalDateTime start, LocalDateTime end);
}
