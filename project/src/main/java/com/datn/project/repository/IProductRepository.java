package com.datn.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datn.project.entity.Product;

public interface IProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    List<Product> findTop10ByDeletedAtIsNullOrderByCreatedAtDesc();

    List<Product> findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

    List<Product> findByDeletedAtIsNull();

    @Query("""
                SELECT p FROM Product p
                JOIN FETCH p.category
                JOIN FETCH p.brand
                JOIN FETCH p.targetAudience
                WHERE p.id = :id AND p.deletedAt IS NULL
            """)
    Optional<Product> findDetailById(@Param("id") int id);
}
