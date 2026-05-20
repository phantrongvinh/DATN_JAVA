package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Order;

public interface IOrderRepository extends JpaRepository<Order, Integer> {

}
