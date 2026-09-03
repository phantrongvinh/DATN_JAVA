package com.datn.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.service.ShippingSimulatorService;

@RestController
@RequestMapping(value = "/api/v1/shipping-simulator")
public class ShippingSimulatorController {

    @Autowired
    private ShippingSimulatorService service;

    @PostMapping("/orders/{orderId}/picked")
    public ResponseEntity<?> picked(
            @PathVariable Integer orderId) {
        service.sendStatus(orderId, "picked");
        return ResponseEntity.ok("Đã giả lập lấy hàng");
    }

    @PostMapping("/orders/{orderId}/delivering")
    public ResponseEntity<?> delivering(
            @PathVariable Integer orderId) {
        service.sendStatus(orderId, "delivering");
        return ResponseEntity.ok("Đã giả lập đang giao");
    }

    @PostMapping("/orders/{orderId}/delivered")
    public ResponseEntity<?> delivered(
            @PathVariable Integer orderId) {
        service.sendStatus(orderId, "delivered");
        return ResponseEntity.ok("Đã giả lập giao thành công");
    }

    @PostMapping("/returns/{returnId}/picked")
    public ResponseEntity<?> returnPicked(@PathVariable Integer returnId) {
        service.sendReturnStatus(returnId, "picked");
        return ResponseEntity.ok("Đã lấy hàng trả");
    }

    @PostMapping("/returns/{returnId}/delivering")
    public ResponseEntity<?> returnDelivering(@PathVariable Integer returnId) {
        service.sendReturnStatus(returnId, "delivering");
        return ResponseEntity.ok("Đã đang giao hàng trả về shop");
    }

    @PostMapping("/returns/{returnId}/delivered")
    public ResponseEntity<?> returnDelivered(@PathVariable Integer returnId) {
        service.sendReturnStatus(returnId, "delivered");
        return ResponseEntity.ok("Đã trả hàng thành công");
    }
}
