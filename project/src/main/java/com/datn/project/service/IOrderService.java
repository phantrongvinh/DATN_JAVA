package com.datn.project.service;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;

import com.datn.project.dto.order.OrderRequest;
import com.datn.project.dto.order.OrderResponse;
import com.datn.project.entity.Order;

public interface IOrderService {
    ResponseEntity<?> placeOrder(Integer userId, OrderRequest request) throws BadRequestException;

    OrderResponse mapToResponse(Order order);

    Order findById(Integer orderId);

    void confirmPayment(Integer orderId, String transactionId);

    ResponseEntity<?> getOrdersByIdByUser(int id, int userid);
}
