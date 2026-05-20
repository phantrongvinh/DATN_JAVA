package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Category;

public interface ICategoryRepository extends JpaRepository<Category, Integer> {

}
