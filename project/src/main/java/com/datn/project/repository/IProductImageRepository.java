package com.datn.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datn.project.entity.ProductImage;

import jakarta.transaction.Transactional;

public interface IProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductIdOrderByIsPrimaryDesc(Integer productId);

    List<ProductImage> findByProductId(Integer productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId AND pi.id NOT IN :keepIds")
    void deleteByProductIdAndIdNotIn(
            @Param("productId") Integer productId,
            @Param("keepIds") List<Integer> keepIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductImage i WHERE i.product.id = :productId")
    void deleteByProductId(@Param("productId") Integer productId);

    @Query("""
                SELECT pi.product.id, pi.imageUrl, pi.isPrimary
                FROM ProductImage pi
                WHERE pi.product.id IN :productIds
                ORDER BY pi.product.id ASC, pi.isPrimary DESC, pi.id ASC
            """)
    List<Object[]> findImagesByProductIds(List<Integer> productIds);

    List<ProductImage> findByProductIdAndIdNotIn(
            Integer productId,
            List<Integer> keepIds
    );
}
