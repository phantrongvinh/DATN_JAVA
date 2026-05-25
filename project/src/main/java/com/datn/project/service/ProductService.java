package com.datn.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.datn.project.dto.ProductResponse;
import com.datn.project.dto.ProductSpotlightResponse;
import com.datn.project.dto.ProductVariantResponse;
import com.datn.project.entity.Product;
import com.datn.project.repository.IProductRepository;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Override
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products not found");
        }

        List<ProductResponse> responses = products.stream().map(p -> {
            ProductResponse response = new ProductResponse();

            response.setName(p.getName());
            response.setDescription(p.getDescription());
            response.setBasePrice(p.getBasePrice());
            response.setCreatedAt(p.getCreatedAt());
            response.setCategory(p.getCategory().getName());
            response.setBrand(p.getBrand().getName());
            response.setTargetAudience(p.getTargetAudience().getName());
            response.setAccessory(p.getCategory().isAccessory());

            List<ProductVariantResponse> variantResponses = p.getProductVariants().stream().map(v -> {
                ProductVariantResponse variantResponse = new ProductVariantResponse();

                variantResponse.setColor(v.getColor());
                variantResponse.setSize(v.getSize().getName());
                variantResponse.setStock(v.getStock());
                variantResponse.setSku(v.getSku());
                variantResponse.setCreatedAt(v.getCreatedAt());

                return variantResponse;

            }).toList();

            response.setProductVariant(variantResponses);

            return response;
        }).toList();

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<?> getSpotlightProducts() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products not found");
        }

        List<ProductSpotlightResponse> responses = products.stream().map(p -> {
            ProductSpotlightResponse response = new ProductSpotlightResponse();

            response.setId(p.getId());
            response.setName(p.getName());
            if (p.getProductImages() != null && !p.getProductImages().isEmpty()) {
                response.setImg(p.getProductImages().get(0).getImageUrl());
            }

            return response;
        }).toList();

        return ResponseEntity.ok(responses);
    }
}
