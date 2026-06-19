package com.datn.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.dto.ProductFilterDTO;
import com.datn.project.dto.ProductUpdateRequest;
import com.datn.project.service.IProductService;

@RestController
@RequestMapping(value = "/api/v1/admin")
public class AdminController {

    @Autowired
    private IProductService productService;

    @GetMapping("/products/top5")
    public ResponseEntity<?> getTop5Product() {
        return ResponseEntity.ok(productService.getTop5Product()).getBody();
    }

    @GetMapping("/products/all")
    public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) List<Integer> audienceIds,
            @RequestParam(required = false) List<Integer> brandIds,
            @RequestParam(required = false) List<Integer> categoryIds) {
                ProductFilterDTO filter = new ProductFilterDTO(
                brandIds,
                categoryIds,
                audienceIds, null);
        return ResponseEntity.ok(productService.getAllProducts(page, size,filter)).getBody();
    }

    @PatchMapping("/products/delete/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable int id) {
        return ResponseEntity.ok(productService.deleteProductById(id)).getBody();
    }

    @PutMapping("/products/update")
    public ResponseEntity<?> updateProductById(@RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(request)).getBody();
    }
}
