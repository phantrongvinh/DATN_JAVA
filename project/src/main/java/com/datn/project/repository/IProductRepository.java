package com.datn.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Product;

public interface IProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findTop10ByOrderByCreatedAtDesc();
}
