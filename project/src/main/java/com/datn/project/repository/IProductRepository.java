package com.datn.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.datn.project.entity.Product;

public interface IProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    List<Product> findTop10ByOrderByCreatedAtDesc();

    List<Product> findTop5ByOrderByCreatedAtDesc();
}
