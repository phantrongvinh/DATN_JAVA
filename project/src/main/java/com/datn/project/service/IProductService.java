package com.datn.project.service;

import org.springframework.http.ResponseEntity;

public interface IProductService {
    
    ResponseEntity<?> getFilterProducts(String audience, String brand);

    ResponseEntity<?> getSpotlightProducts();

    ResponseEntity<?> getTop5Product();
}
