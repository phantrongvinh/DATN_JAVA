package com.datn.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.datn.project.security.CustomUserDetail;
import com.datn.project.service.IReturnRequestService;

@RestController
@RequestMapping(value = "/api/v1/returns")
public class ReturnRequestController {

    @Autowired
    private IReturnRequestService returnService;
   

    @PostMapping(value = "/orders/{orderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> requestReturn(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal CustomUserDetail principal,
            @RequestParam String reason,
            @RequestParam(required = false) List<MultipartFile> images) {
        returnService.requestReturn(principal.getUserID(), orderId, reason, images);
        return ResponseEntity.ok(Map.of("message", "Đã gửi yêu cầu trả hàng"));
    }
}
