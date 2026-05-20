package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Product;

public interface IProductRepository extends JpaRepository<Product, Integer> {

}
