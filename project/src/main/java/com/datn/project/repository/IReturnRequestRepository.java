package com.datn.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.ReturnRequest;
import com.datn.project.entity.ReturnStatus;

public interface IReturnRequestRepository extends JpaRepository<ReturnRequest, Integer> {
    Optional<ReturnRequest> findByOrderIdAndStatus(Integer orderId, ReturnStatus status);

    List<ReturnRequest> findAllByOrderByCreatedAtDesc();
}