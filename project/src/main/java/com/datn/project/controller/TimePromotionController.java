package com.datn.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.service.ITimePromotionService;

@RestController
@RequestMapping(value = "/api/v1/time-promotions")
public class TimePromotionController {

    @Autowired
    private ITimePromotionService timePromotionService;

    @GetMapping("/active")
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(timePromotionService.getActiveTimePromotion().orElse(null));
    }
}
