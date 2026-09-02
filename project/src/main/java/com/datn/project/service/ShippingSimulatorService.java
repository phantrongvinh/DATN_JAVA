package com.datn.project.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.datn.project.entity.Order;
import com.datn.project.entity.OrderStatus;
import com.datn.project.repository.IOrderRepository;

import jakarta.transaction.Transactional;

@Service
public class ShippingSimulatorService {

    @Autowired
    private IOrderRepository orderRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public void sendStatus(Integer orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getTrackingCode() == null) {
            throw new RuntimeException(
                    "Đơn hàng chưa có mã vận đơn GHN"
            );
        }

        Map<String, Object> payload = new LinkedHashMap<>();

        payload.put("OrderCode", order.getTrackingCode());
        payload.put("Status", status);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(
                baseUrl + "/api/v1/webhook/ghn",
                request,
                Void.class
        );
    }
}
