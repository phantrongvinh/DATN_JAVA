package com.datn.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Order;

public interface IOrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

}
