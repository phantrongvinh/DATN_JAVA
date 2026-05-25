package com.datn.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.service.IProductService;

@RestController
@RequestMapping(value = "/api/v1/products")
public class ProductController {
    
    @Autowired
    private IProductService productService;

    @GetMapping()
    public ResponseEntity<?> getFilterProducts(@RequestParam(required = false)String audience,@RequestParam(required = false) String brand ) {
        return ResponseEntity.ok(productService.getFilterProducts(audience,brand)).getBody();
    }

    @GetMapping("/spotlight")
    public ResponseEntity<?> getSpotlightProducts() {
        return ResponseEntity.ok(productService.getSpotlightProducts()).getBody();
    }
}
