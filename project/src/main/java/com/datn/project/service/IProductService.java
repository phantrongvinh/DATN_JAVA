package com.datn.project.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.datn.project.dto.ProductUpdateRequest;

public interface IProductService {
    
    ResponseEntity<?> getFilterProducts(List<String> audiences, List<String> brands);

    ResponseEntity<?> getSpotlightProducts();

    ResponseEntity<?> getTop5Product();

    ResponseEntity<?> getAllProducts(int page, int size);

    ResponseEntity<?> deleteProductById(int id);

    ResponseEntity<?> updateProduct(ProductUpdateRequest request);
}
