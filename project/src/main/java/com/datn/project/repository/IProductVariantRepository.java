package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.ProductVariant;

public interface IProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

}
