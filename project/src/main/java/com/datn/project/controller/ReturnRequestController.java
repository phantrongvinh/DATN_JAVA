package com.datn.project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.dto.order.ReturnRequestDTO;
import com.datn.project.security.CustomUserDetail;
import com.datn.project.service.IReturnRequestService;

@RestController
@RequestMapping(value = "/api/v1/return")
public class ReturnRequestController {

    @Autowired
    IReturnRequestService returnService;

    @PostMapping("/orders/{orderId}/return")
    public ResponseEntity<?> requestReturn(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal CustomUserDetail principal,
            @RequestBody ReturnRequestDTO dto) {
        returnService.requestReturn(principal.getUserID(), orderId, dto.getReason(), dto.getImages());
        return ResponseEntity.ok(Map.of("message", "Đã gửi yêu cầu trả hàng"));
    }
}
