package com.datn.project.service;

import org.springframework.http.ResponseEntity;

import com.datn.project.dto.ProductUpdateRequest;

public interface IProductService {
    
    ResponseEntity<?> getFilterProducts(String audience, String brand);

    ResponseEntity<?> getSpotlightProducts();

    ResponseEntity<?> getTop5Product();

    ResponseEntity<?> getAllProducts(int page, int size);

    ResponseEntity<?> deleteProductById(int id);

    ResponseEntity<?> updateProduct(ProductUpdateRequest request);
}
