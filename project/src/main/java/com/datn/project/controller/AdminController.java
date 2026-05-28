package com.datn.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.service.IProductService;

@RestController
@RequestMapping(value = "/api/v1/admin")
public class AdminController {

    @Autowired
    private IProductService productService;

    @GetMapping("/products")
    public ResponseEntity<?> getTop5Product() {
        return ResponseEntity.ok(productService.getTop5Product()).getBody();
    }
    
}
