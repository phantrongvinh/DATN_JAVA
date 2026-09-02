package com.datn.project.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.datn.project.entity.Order;
import com.datn.project.entity.OrderStatus;
import com.datn.project.repository.IOrderRepository;

@Service
public class GHNService {

    @Value("${ghn.token}")
    private String token;
    @Value("${ghn.shop-id}")
    private String shopId;
    @Value("${ghn.url}")
    private String url;

    @Autowired
    private IOrderRepository orderRepository;

    private RestTemplate restTemplate = new RestTemplate();

    public void createShipment(Integer orderId) {
        try {
            Order order = orderRepository.findByIdWithDetails(orderId).orElseThrow();

            // Lấy địa chỉ từ order (cần lưu districtId, wardCode vào Order)
            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", token);
            headers.set("ShopId", shopId);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("to_name", order.getReceiverName());
            body.put("to_phone", order.getReceiverPhone());
            body.put("to_address", order.getShippingDetail());
            body.put("to_ward_code", order.getToWardCode()); 
            body.put("to_district_id", order.getToDistrictId()); 
            body.put("weight", 500);
            body.put("length", 20);
            body.put("width", 15);
            body.put("height", 10);
            body.put("service_type_id", 2);
            body.put("payment_type_id", 1);
            body.put("required_note", "KHONGCHOXEMHANG");
            body.put("from_name", "DATN Shop");
            body.put("from_phone", "0909090909");
            body.put("from_address", "123 Nguyen Hue");
            body.put("from_ward_name", "Phường Bến Nghé");
            body.put("from_district_name", "Quận 1");
            body.put("from_province_name", "TP Hồ Chí Minh");

            body.put("items", order.getOrderDetails().stream().map(d -> Map.of(
                    "name", d.getProductName(),
                    "quantity", d.getQuantity(),
                    "price", d.getPrice().intValue())).toList());

            HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
            ResponseEntity<Map> res = restTemplate.postForEntity(
                    url + "/shiip/public-api/v2/shipping-order/create", req, Map.class);

            if (res.getStatusCode().is2xxSuccessful()) {
                String trackingCode = (String) ((Map) res.getBody().get("data")).get("order_code");
                order.setTrackingCode(trackingCode);
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
            }
            
        } catch (HttpClientErrorException e) {
            System.out.println("=== GHN error: " + e.getResponseBodyAsString());
        }
    }

    public List<Map> getProvinces() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);

        HttpEntity<?> req = new HttpEntity<>(headers);
        ResponseEntity<Map> res = restTemplate.exchange(
                url + "/shiip/public-api/master-data/province",
                HttpMethod.GET, req, Map.class);
        return (List<Map>) res.getBody().get("data");
    }

    public List<Map> getDistricts(Integer provinceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);

        HttpEntity<?> req = new HttpEntity<>(headers);
        ResponseEntity<Map> res = restTemplate.exchange(
                url + "/shiip/public-api/master-data/district?province_id=" + provinceId,
                HttpMethod.GET, req, Map.class);
        return (List<Map>) res.getBody().get("data");
    }

    public List<Map> getWards(Integer districtId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);

        HttpEntity<?> req = new HttpEntity<>(headers);
        ResponseEntity<Map> res = restTemplate.exchange(
                url + "/shiip/public-api/master-data/ward?district_id=" + districtId,
                HttpMethod.GET, req, Map.class);
        return (List<Map>) res.getBody().get("data");
    }

    public BigDecimal calculateShippingFee(
            Integer toDistrictId,
            String toWardCode,
            Integer weight) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.set("ShopId", shopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service_type_id", 2);
        body.put("to_district_id", toDistrictId);
        body.put("to_ward_code", toWardCode);
        body.put("weight", weight);
        body.put("length", 20);
        body.put("width", 15);
        body.put("height", 10);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                url + "/shiip/public-api/v2/shipping-order/fee",
                request,
                Map.class);

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");

        Number total = (Number) data.get("total");

        return BigDecimal.valueOf(total.longValue());
    }

    public String createReturnOrder(Order order) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.set("ShopId", shopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        // Điểm ĐẾN = shop (nhận hàng trả về)
        body.put("to_name", "DATN Shop");
        body.put("to_phone", "0909090909");
        body.put("to_address", "123 Nguyen Hue");
        body.put("to_ward_name", "Phường Bến Nghé");
        body.put("to_district_name", "Quận 1");
        body.put("to_province_name", "TP Hồ Chí Minh");

        // Điểm LẤY = khách hàng (giao vận đơn gốc)
        body.put("from_name", order.getReceiverName());
        body.put("from_phone", order.getReceiverPhone());
        body.put("from_address", order.getShippingDetail());
        body.put("from_ward_name", null); 
        body.put("from_district_name", null); 

        body.put("weight", 500);
        body.put("length", 20);
        body.put("width", 15);
        body.put("height", 10);
        body.put("service_type_id", 2);
        body.put("payment_type_id", 1); // shop trả phí ship trả hàng
        body.put("required_note", "KHONGCHOXEMHANG");
        body.put("items", order.getOrderDetails().stream().map(d -> Map.of(
                "name", d.getProductName(),
                "quantity", d.getQuantity(),
                "price", d.getPrice().intValue())).toList());

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        ResponseEntity<Map> res = restTemplate.postForEntity(
                url + "/shiip/public-api/v2/shipping-order/create", req, Map.class);

        if (res.getStatusCode().is2xxSuccessful()) {
            return (String) ((Map) res.getBody().get("data")).get("order_code");
        }
        return null;
    } catch (HttpClientErrorException e) {
        System.out.println("=== GHN return error: " + e.getResponseBodyAsString());
        return null; 
    }
}
}
