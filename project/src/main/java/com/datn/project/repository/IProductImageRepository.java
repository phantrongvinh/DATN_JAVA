package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.ProductImage;

public interface IProductImageRepository extends JpaRepository<ProductImage, Integer> {

}
