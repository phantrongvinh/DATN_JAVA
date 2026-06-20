package com.datn.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.datn.project.dto.ProductDetailDTO;
import com.datn.project.dto.ProductFilterDTO;
import com.datn.project.dto.ProductImageDTO;
import com.datn.project.dto.ProductOverview;
import com.datn.project.dto.ProductResponse;
import com.datn.project.dto.ProductSpotlightResponse;
import com.datn.project.dto.ProductUpdateRequest;
import com.datn.project.dto.ProductVariantDTO;
import com.datn.project.dto.ProductVariantResponse;
import com.datn.project.entity.Brand;
import com.datn.project.entity.Category;
import com.datn.project.entity.Product;
import com.datn.project.repository.IBrandRepository;
import com.datn.project.repository.ICategoryRepository;
import com.datn.project.repository.IProductImageRepository;
import com.datn.project.repository.IProductRepository;
import com.datn.project.repository.IProductVariantRepository;
import com.datn.project.specification.ProductSpecification;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IBrandRepository brandRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private IProductImageRepository productImageRepository;

    @Autowired
    private IProductVariantRepository productVariantRepository;


    // Lấy tất cả product theo filter và không bị vô hiệu hóa
    @Override
    public ResponseEntity<?> getFilterProducts(ProductFilterDTO filterDTO) {
        List<Product> products = productRepository.findAll(ProductSpecification.filter(filterDTO));

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

    // lấy 10 product mới nhất để hiện lên index
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

    // lấy 5 product mới nhất để thống kê
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

    // Lấy tất cả product có filter và page để quản lý ở admin
    @Override
    public ResponseEntity<?> getAllProducts(int page, int size, ProductFilterDTO filterDTO) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> products = productRepository.findAll(ProductSpecification.adminFilter(filterDTO),pageable);

        if (products.isEmpty()) {
            return ResponseEntity.ok(Map.of("message","Không tìm thấy sản phẩm"));
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

    // Vô hiệu hóa và khôi phục product theo id
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

    // Cập nhật product theo id
    @Override
    public ResponseEntity<?> updateProduct(ProductUpdateRequest request) {
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getName().equals(request.getName()) &&
                product.getBasePrice().equals(request.getPrice()) &&
                product.getBrand().getId() == request.getBrandID() &&
                product.getCategory().getId() == request.getCategoryID()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No data change");
        }

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

    // lấy product theo id gồm các biến thể và hình ảnh của product
    @Override
    public ResponseEntity<?> getProductDetail(int id) {
        Product product = productRepository.findDetailById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        List<ProductImageDTO> images = productImageRepository
                .findByProductIdOrderByIsPrimaryDesc(id)
                .stream()
                .map(img -> ProductImageDTO.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .isPrimary(img.getIsPrimary())
                        .build())
                .toList();

        List<ProductVariantDTO> variants = productVariantRepository
                .findByProductId(id)
                .stream()
                .map(v -> ProductVariantDTO.builder()
                        .id(v.getId())
                        .color(v.getColor())
                        .sizeId(v.getSize().getId())
                        .sizeName(v.getSize().getName())
                        .stock(v.getStock())
                        .price(v.getPrice())
                        .sku(v.getSku())
                        .build())
                .toList();

        ProductDetailDTO dto = ProductDetailDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .brandId(product.getBrand().getId())
                .brandName(product.getBrand().getName())
                .brandLogo(product.getBrand().getLogo())
                .targetAudienceId(product.getTargetAudience().getId())
                .targetAudienceName(product.getTargetAudience().getName())
                .images(images)
                .variants(variants)
                .build();

        return ResponseEntity.ok(dto);
    }
}
