package com.datn.project.service;

import org.springframework.http.ResponseEntity;

import com.datn.project.dto.product.ProductFilterDTO;
import com.datn.project.dto.product.ProductRequest;

public interface IProductService {

    ResponseEntity<?> getFilterProducts(ProductFilterDTO filterDTO, int page, int size);

    ResponseEntity<?> getSpotlightProducts();

    ResponseEntity<?> getTop5Product();

    ResponseEntity<?> getAllProducts(int page, int size, ProductFilterDTO filterDTO);

    ResponseEntity<?> deactivateProduct(Integer id);

    ResponseEntity<?> updateProduct(Integer id, ProductRequest request);

    ResponseEntity<?> getProductDetail(int id);

    ResponseEntity<?> getProductOnSale();

    ResponseEntity<?> createProduct(ProductRequest request);

}
