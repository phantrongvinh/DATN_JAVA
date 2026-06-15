package com.datn.project.service;


import org.springframework.http.ResponseEntity;

import com.datn.project.dto.ProductFilterDTO;
import com.datn.project.dto.ProductUpdateRequest;

public interface IProductService {
    
    ResponseEntity<?> getFilterProducts(ProductFilterDTO filterDTO);

    ResponseEntity<?> getSpotlightProducts();

    ResponseEntity<?> getTop5Product();

    ResponseEntity<?> getAllProducts(int page, int size,ProductFilterDTO filterDTO);

    ResponseEntity<?> deleteProductById(int id);

    ResponseEntity<?> updateProduct(ProductUpdateRequest request);
}
