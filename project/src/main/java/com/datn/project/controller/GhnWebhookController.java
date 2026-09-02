package com.datn.project.controller;

import com.datn.project.entity.Order;
import com.datn.project.entity.OrderStatus;
import com.datn.project.entity.ReturnRequest;
import com.datn.project.repository.IOrderRepository;
import com.datn.project.repository.IReturnRequestRepository;
import com.datn.project.service.IOrderService;
import com.datn.project.service.IReturnRequestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/webhook/ghn")
public class GhnWebhookController {

    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IOrderService orderService;

    @Autowired
    private IReturnRequestService returnService;

    @Autowired
    private IReturnRequestRepository returnRequestRepository;

    @PostMapping
    public void handleWebhook(@RequestBody Map<String, Object> payload) {
        String trackingCode = (String) payload.get("OrderCode");
        String ghnStatus = (String) payload.get("Status");

        Optional<ReturnRequest> returnReq = returnRequestRepository.findByGhnReturnCode(trackingCode);
        if (returnReq.isPresent()) {
            if ("delivered".equals(ghnStatus)) {
                returnService.completeReturn(returnReq.get().getId());
            }
            return;
        }

        Order order = orderRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn: " + trackingCode));

        OrderStatus newStatus = mapGhnStatus(ghnStatus);
        if (newStatus != null) {
            orderService.updateOrderStatus(order.getId(), newStatus);
        }   
    }

    private OrderStatus mapGhnStatus(String ghnStatus) {
        return switch (ghnStatus) {
            case "picked" -> OrderStatus.PICKED;
            case "delivering" -> OrderStatus.SHIPPING;
            case "delivered" -> OrderStatus.DELIVERED;
            case "cancel" -> OrderStatus.CANCELLED;
            default -> null;
        };
    }
}