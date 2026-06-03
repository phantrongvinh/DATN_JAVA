package com.datn.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.datn.project.dto.ProductOverview;
import com.datn.project.dto.ProductResponse;
import com.datn.project.dto.ProductSpotlightResponse;
import com.datn.project.dto.ProductUpdateRequest;
import com.datn.project.dto.ProductVariantResponse;
import com.datn.project.entity.Brand;
import com.datn.project.entity.Category;
import com.datn.project.entity.Product;
import com.datn.project.repository.IBrandRepository;
import com.datn.project.repository.ICategoryRepository;
import com.datn.project.repository.IProductRepository;
import com.datn.project.specification.ProductSpecification;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IBrandRepository brandRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Override
    public ResponseEntity<?> getFilterProducts(String audience, String brand) {
        List<Product> products = productRepository.findAll(ProductSpecification.filter(audience, brand));

        if (products.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Product not found"));
        }

        List<ProductResponse> responses = products.stream().map(p -> {
            ProductResponse response = new ProductResponse();

            response.setId(p.getId());
            response.setName(p.getName());
            response.setDescription(p.getDescription());
            response.setBasePrice(p.getBasePrice());
            response.setCreatedAt(p.getCreatedAt());
            response.setCategory(p.getCategory().getName());
            response.setBrand(p.getBrand().getName());
            response.setTargetAudience(p.getTargetAudience().getName());
            response.setAccessory(p.getCategory().isAccessory());
            if (p.getProductImages() != null && !p.getProductImages().isEmpty()) {
                response.setImg(p.getProductImages().get(0).getImageUrl());
            }

            List<ProductVariantResponse> variantResponses = p.getProductVariants().stream().map(v -> {
                ProductVariantResponse variantResponse = new ProductVariantResponse();

                variantResponse.setId(v.getId());
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
        List<Product> products = productRepository.findTop10ByDeletedAtIsNullOrderByCreatedAtDesc();

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

    @Override
    public ResponseEntity<?> getTop5Product() {
        List<Product> products = productRepository.findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products not found");
        }

        List<ProductOverview> res = products.stream().map(p -> {
            ProductOverview overview = new ProductOverview();

            overview.setId(p.getId());
            overview.setName(p.getName());
            overview.setCategory(p.getCategory().getName());
            overview.setBrand(p.getBrand().getName());
            overview.setBasePrice(p.getBasePrice());
            overview.setStock(p.getProductVariants().stream().mapToInt(v -> v.getStock()).sum());

            return overview;
        }).toList();

        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<?> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> products = productRepository.findAll(pageable);

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products not found");
        }

        Page<ProductOverview> responses = products.map(p -> {
            ProductOverview overview = new ProductOverview();

            overview.setId(p.getId());
            overview.setName(p.getName());
            overview.setCategory(p.getCategory().getName());
            overview.setBrand(p.getBrand().getName());
            overview.setBasePrice(p.getBasePrice());
            // overview.setStock(p.getProductVariants().stream().mapToInt(v ->
            // v.getStock()).sum());
            overview.setStatus(p.getDeletedAt() == null ? false : true);
            overview.setUpdatedAt(p.getUpdatedAt());
            overview.setVariantCount(p.getProductVariants().size());

            return overview;

        });

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<?> deleteProductById(int id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getDeletedAt() != null) {
            product.setDeletedAt(null);

        } else {
            product.setDeletedAt(LocalDateTime.now());
        }

        productRepository.save(product);

        return ResponseEntity.ok("Delete product successfully");
    }

    @Override
    public ResponseEntity<?> updateProduct(ProductUpdateRequest request) {
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setBasePrice(request.getPrice());
        product.setName(request.getName());

        Brand brand = brandRepository.findById(request.getBrandID())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        product.setBrand(brand);

        Category category = categoryRepository.findById(request.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setCategory(category);

        productRepository.save(product);
        return ResponseEntity.ok("Update product successfully");
    }
}
