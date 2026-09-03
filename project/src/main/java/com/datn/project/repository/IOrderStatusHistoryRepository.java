package com.datn.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.OrderStatusHistory;

public interface IOrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory,Integer> {
     List<OrderStatusHistory> findByOrderIdOrderByCreatedAtDesc(Integer orderId);
    
}
