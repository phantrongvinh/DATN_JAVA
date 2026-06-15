package com.datn.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.dto.ProductFilterDTO;
import com.datn.project.service.IProductService;

@RestController
@RequestMapping(value = "/api/v1/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping()
    public ResponseEntity<?> getFilterProducts(@RequestParam(required = false) List<Integer> audienceIds,
            @RequestParam(required = false) List<Integer> brandIds,
            @RequestParam(required = false) List<Integer> categoryIds) {
        ProductFilterDTO filter = new ProductFilterDTO(
                brandIds,
                categoryIds,
                audienceIds, null);
        return ResponseEntity.ok(productService.getFilterProducts(filter)).getBody();
    }

    @GetMapping("/spotlight")
    public ResponseEntity<?> getSpotlightProducts() {
        return ResponseEntity.ok(productService.getSpotlightProducts()).getBody();
    }
}
