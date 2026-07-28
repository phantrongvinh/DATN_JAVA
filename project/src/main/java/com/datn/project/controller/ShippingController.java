package com.datn.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.service.GHNService;

@RestController
@RequestMapping("/api/v1/shipping")
public class ShippingController {

    @Autowired
    private GHNService ghnService;

     @GetMapping("/provinces")
    public ResponseEntity<?> getProvinces() {
        return ResponseEntity.ok(ghnService.getProvinces());
    }

    @GetMapping("/districts")
    public ResponseEntity<?> getDistricts(@RequestParam Integer provinceId) {
        return ResponseEntity.ok(ghnService.getDistricts(provinceId));
    }

    @GetMapping("/wards")
    public ResponseEntity<?> getWards(@RequestParam Integer districtId) {
        return ResponseEntity.ok(ghnService.getWards(districtId));
    }

    @GetMapping("/fee")
    public ResponseEntity<?> getFee(
        @RequestParam Integer toDistrictId,
        @RequestParam String toWardCode,
        @RequestParam(defaultValue = "500") Integer weight
    ) {
        return ResponseEntity.ok(ghnService.calculateShippingFee(toDistrictId, toWardCode, weight));
    }
}
