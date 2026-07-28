package com.datn.project.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.datn.project.entity.ProductReview;
import com.datn.project.entity.ProductReviewId;

public interface IProductReviewRepository extends JpaRepository<ProductReview, ProductReviewId> {

    List<ProductReview> findByIdProductIdAndIsVisibleTrueOrderByCreatedAtDesc(Integer productId);

    boolean existsById_UserIdAndId_ProductId(Integer userId, Integer productId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.id.productId = :productId AND r.isVisible = true")
    Double findAverageRating(Integer productId);

    @Query("""
        SELECT r.rating, COUNT(r)
        FROM ProductReview r
        WHERE r.id.productId = :productId AND r.isVisible = true
        GROUP BY r.rating
    """)
    List<Object[]> countByRatingGrouped(Integer productId);

    @Query("""
        SELECT r.id.productId, COUNT(r)
        FROM ProductReview r
        WHERE r.createdAt >= :start AND r.createdAt < :end
        GROUP BY r.id.productId
    """)
    List<Object[]> countReviewsByProductInRange(LocalDateTime start, LocalDateTime end);
}
