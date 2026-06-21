package com.datn.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datn.project.entity.ProductVariant;

public interface IProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    @Query("""
                SELECT v FROM ProductVariant v
                JOIN FETCH v.size
                WHERE v.product.id = :productId
            """)
    List<ProductVariant> findByProductId(@Param("productId") Integer productId);
}
