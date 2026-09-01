package com.datn.project.controller;

import com.datn.project.entity.Order;
import com.datn.project.entity.OrderStatus;
import com.datn.project.repository.IOrderRepository;
import com.datn.project.service.IOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhook/ghn")
public class GhnWebhookController {

    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IOrderService orderService;

    @PostMapping
    public void handleWebhook(@RequestBody Map<String, Object> payload) {
        String ghnOrderCode = (String) payload.get("OrderCode");
        String ghnStatus = (String) payload.get("Status");

        Order order = orderRepository.findByGhnOrderCode(ghnOrderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng GHN: " + ghnOrderCode));

        OrderStatus newStatus = mapGhnStatus(ghnStatus);
        if (newStatus != null) {
            orderService.updateOrderStatus(order.getId(), newStatus); 
        }
    }

    private OrderStatus mapGhnStatus(String ghnStatus) {
        return switch (ghnStatus) {
            case "delivered" -> OrderStatus.DELIVERED;
            case "delivering" -> OrderStatus.SHIPPING;
            case "cancel" -> OrderStatus.CANCELLED;
            default -> null; 
        };
    }
}