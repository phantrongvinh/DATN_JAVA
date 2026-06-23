package com.datn.project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.entity.Order;
import com.datn.project.service.GHNService;
import com.datn.project.service.IOrderService;
import com.datn.project.service.VNPayService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/payment")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private GHNService ghnService;

    @Autowired
    private IOrderService orderService;

    // Tạo payment URL sau khi order đã được tạo
    @GetMapping("/vnpay/{orderId}")
    public ResponseEntity<?> vnpayPayment(
            @PathVariable Integer orderId,
            HttpServletRequest request) throws Exception {
        Order order = orderService.findById(orderId);
        String paymentUrl = vnPayService.createPaymentUrl(
                orderId,
                order.getFinalPrice(),
                request.getRemoteAddr());
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    // @GetMapping("/momo/{orderId}")
    // public ResponseEntity<?> momoPayment(@PathVariable Integer orderId) throws
    // Exception {
    // Order order = orderService.findById(orderId);
    // String paymentUrl = moMoService.createPaymentUrl(orderId,
    // order.getFinalPrice());
    // return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    // }

    // VNPay callback (redirect từ VNPay về)
    @GetMapping("/vnpay/callback")
    public ResponseEntity<?> vnpayCallback(@RequestParam Map<String, String> params) throws Exception {
        boolean isValid = vnPayService.verifyCallback(params);

        if (isValid && "00".equals(params.get("vnp_ResponseCode"))) {
            String txnRef = params.get("vnp_TxnRef");
            Integer orderId = Integer.parseInt(txnRef.split("_")[0]);

            orderService.confirmPayment(orderId, params.get("vnp_TransactionNo"));
            ghnService.createShipment(orderId); // gọi API giao hàng
            return ResponseEntity.ok(Map.of("message", "Thanh toán thành công"));
        }

        return ResponseEntity.badRequest().body(Map.of("message", "Thanh toán thất bại"));
    }

}
