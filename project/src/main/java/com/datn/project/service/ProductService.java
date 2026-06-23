package com.datn.project.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.datn.project.dto.PromotionResponse;
import com.datn.project.dto.product.ProductDetailDTO;
import com.datn.project.dto.product.ProductFilterDTO;
import com.datn.project.dto.product.ProductImageDTO;
import com.datn.project.dto.product.ProductOverview;
import com.datn.project.dto.product.ProductResponse;
import com.datn.project.dto.product.ProductSpotlightResponse;
import com.datn.project.dto.product.ProductUpdateRequest;
import com.datn.project.dto.product.ProductVariantDTO;
import com.datn.project.dto.product.ProductVariantResponse;
import com.datn.project.entity.Brand;
import com.datn.project.entity.Category;
import com.datn.project.entity.Product;
import com.datn.project.entity.ProductImage;
import com.datn.project.entity.ProductVariant;
import com.datn.project.entity.Promotion;
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

        @Autowired
        private IPromotionService promotionService;

        // config để lấy product và giảm giá tốt nhất
        private Optional<Promotion> getBestPromotion(Product product, BigDecimal price) {

                LocalDateTime now = LocalDateTime.now();

                return product.getPromotions()
                                .stream()
                                .filter(pr -> !pr.getStartAt().isAfter(now)
                                                && !pr.getEndAt().isBefore(now))
                                .min(Comparator.comparing(
                                                pr -> promotionService.calcDiscountedPrice(price, pr)));
        }

        private PromotionResponse toPromotionResponse(Promotion promotion) {

                PromotionResponse response = new PromotionResponse();

                response.setId(promotion.getId());
                response.setName(promotion.getName());
                response.setDiscountType(
                                promotion.getDiscountType().name().toLowerCase());
                response.setDiscountValue(promotion.getDiscountValue());
                response.setStartAt(promotion.getStartAt());
                response.setEndAt(promotion.getEndAt());

                return response;
        }

        private ProductVariantResponse toVariantResponse(
                        ProductVariant variant,
                        Optional<Promotion> promotion) {

                ProductVariantResponse response = new ProductVariantResponse();

                response.setId(variant.getId());
                response.setColor(variant.getColor());
                response.setSize(variant.getSize().getName());
                response.setStock(variant.getStock());
                response.setSku(variant.getSku());
                response.setPrice(variant.getPrice());

                response.setDiscountedPrice(
                                promotion
                                                .map(pr -> promotionService.calcDiscountedPrice(
                                                                variant.getPrice(), pr))
                                                .orElse(variant.getPrice()));

                response.setCreatedAt(variant.getCreatedAt());

                return response;
        }

        private ProductResponse toResponse(Product p) {

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

                response.setImg(
                                p.getProductImages()
                                                .stream()
                                                .findFirst()
                                                .map(ProductImage::getImageUrl)
                                                .orElse(null));

                BigDecimal minPrice = p.getProductVariants()
                                .stream()
                                .map(ProductVariant::getPrice)
                                .min(BigDecimal::compareTo)
                                .orElse(p.getBasePrice());

                BigDecimal maxPrice = p.getProductVariants()
                                .stream()
                                .map(ProductVariant::getPrice)
                                .max(BigDecimal::compareTo)
                                .orElse(p.getBasePrice());

                Optional<Promotion> promo = getBestPromotion(p, minPrice);

                response.setMinPrice(minPrice);
                response.setMaxPrice(maxPrice);

                response.setDiscountedPrice(
                                promo.map(pr -> promotionService.calcDiscountedPrice(minPrice, pr))
                                                .orElse(minPrice));

                promo.ifPresent(pr -> response.setPromotion(toPromotionResponse(pr)));

                List<ProductVariantResponse> variants = p.getProductVariants()
                                .stream()
                                .map(v -> toVariantResponse(v, promo))
                                .toList();

                response.setProductVariant(variants);

                return response;
        }

        private ProductOverview toOverview(Product p) {
                ProductOverview overview = new ProductOverview();

                overview.setId(p.getId());
                overview.setName(p.getName());
                overview.setCategory(p.getCategory().getName());
                overview.setBrand(p.getBrand().getName());
                overview.setBasePrice(p.getBasePrice());
                overview.setStatus(p.getDeletedAt() != null);
                overview.setUpdatedAt(p.getUpdatedAt());
                overview.setVariantCount(p.getProductVariants().size());

                overview.setStock(
                                p.getProductVariants()
                                                .stream()
                                                .mapToInt(ProductVariant::getStock)
                                                .sum());

                return overview;
        }

        // Lấy tất cả product theo filter và không bị vô hiệu hóa
        @Override
        public ResponseEntity<?> getFilterProducts(ProductFilterDTO filterDTO, int page, int size) {
                Sort sort = switch (filterDTO.getSortBy() == null ? "" : filterDTO.getSortBy()) {
                        case "price_asc" -> Sort.by("basePrice").ascending();
                        case "price_desc" -> Sort.by("basePrice").descending();
                        case "newest" -> Sort.by("createdAt").descending();
                        default -> Sort.by("createdAt").descending();
                };

                Pageable pageable = PageRequest.of(page, size, sort);
                Slice<Product> products = productRepository.findAll(ProductSpecification.filter(filterDTO), pageable);

                if (products.isEmpty()) {
                        return ResponseEntity.ok(
                                        Map.of("message", "Không tìm thấy sản phẩm"));
                }

                return ResponseEntity.ok(Map.of(
                                "content", products.stream().map(this::toResponse).toList(),
                                "hasNext", products.hasNext(),
                                "page", page,
                                "size", size));
        }

        // lấy 10 product mới nhất để hiện lên index

        private ProductSpotlightResponse toSpotlight(Product p) {

                ProductSpotlightResponse response = new ProductSpotlightResponse();

                response.setId(p.getId());
                response.setName(p.getName());

                response.setImg(
                                p.getProductImages()
                                                .stream()
                                                .findFirst()
                                                .map(ProductImage::getImageUrl)
                                                .orElse(null));

                return response;
        }

        @Override
        public ResponseEntity<?> getSpotlightProducts() {
                List<Product> products = productRepository.findTop10ByDeletedAtIsNullOrderByCreatedAtDesc();

                return ResponseEntity.ok(
                                products.stream()
                                                .map(this::toSpotlight)
                                                .toList());
        }

        // lấy 5 product mới nhất để thống kê
        @Override
        public ResponseEntity<?> getTop5Product() {
                return ResponseEntity.ok(
                                productRepository
                                                .findTop5ByDeletedAtIsNullOrderByCreatedAtDesc()
                                                .stream()
                                                .map(this::toOverview)
                                                .toList());
        }

        // Lấy tất cả product có filter và page để quản lý ở admin
        @Override
        public ResponseEntity<?> getAllProducts(int page, int size, ProductFilterDTO filterDTO) {
                Sort sort = switch (filterDTO.getSortBy() == null ? "" : filterDTO.getSortBy()) {
                        case "price_asc" -> Sort.by("basePrice").ascending();
                        case "price_desc" -> Sort.by("basePrice").descending();
                        case "newest" -> Sort.by("createdAt").descending();
                        default -> Sort.by("createdAt").descending();
                };

                Pageable pageable = PageRequest.of(page - 1, size, sort);
                Page<Product> products = productRepository.findAll(ProductSpecification.adminFilter(filterDTO),
                                pageable);

                if (products.isEmpty()) {
                        return ResponseEntity.ok(Map.of("message", "Không tìm thấy sản phẩm"));
                }

                Page<ProductOverview> responses = products.map(this::toOverview);

                return ResponseEntity.ok(responses);

        }

        // Vô hiệu hóa và khôi phục product theo id
        @Override
        public ResponseEntity<?> deleteProductById(int id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product not found"));

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
        private ProductDetailDTO toDetailResponse(Product product) {
                List<ProductImageDTO> images = product.getProductImages()
                                .stream()
                                .sorted((i1, i2) -> Boolean.compare(i2.getIsPrimary(), i1.getIsPrimary()))
                                .map(img -> ProductImageDTO.builder()
                                                .id(img.getId())
                                                .imageUrl(img.getImageUrl())
                                                .isPrimary(img.getIsPrimary())
                                                .build())
                                .toList();

                // Min và Max price
                BigDecimal minPrice = product.getProductVariants()
                                .stream()
                                .map(ProductVariant::getPrice)
                                .min(BigDecimal::compareTo)
                                .orElse(product.getBasePrice());

                BigDecimal maxPrice = product.getProductVariants()
                                .stream()
                                .map(ProductVariant::getPrice)
                                .max(BigDecimal::compareTo)
                                .orElse(product.getBasePrice());

                // Promotion tốt nhất
                Optional<Promotion> promo = getBestPromotion(product, minPrice);

                PromotionResponse promoResponse = promo
                                .map(this::toPromotionResponse)
                                .orElse(null);

                BigDecimal discountedMinPrice = promo
                                .map(pr -> promotionService.calcDiscountedPrice(minPrice, pr))
                                .orElse(minPrice);

                // Variants
                List<ProductVariantDTO> variants = product.getProductVariants()
                                .stream()
                                .map(v -> ProductVariantDTO.builder()
                                                .id(v.getId())
                                                .color(v.getColor())
                                                .sizeId(v.getSize().getId())
                                                .sizeName(v.getSize().getName())
                                                .stock(v.getStock())
                                                .price(v.getPrice())
                                                .discountedPrice(
                                                                promo.map(pr -> promotionService
                                                                                .calcDiscountedPrice(v.getPrice(), pr))
                                                                                .orElse(v.getPrice()))
                                                .sku(v.getSku())
                                                .build())
                                .toList();

                return ProductDetailDTO.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .description(product.getDescription())
                                .basePrice(product.getBasePrice())

                                .minPrice(minPrice)
                                .maxPrice(maxPrice)
                                .discountedMinPrice(discountedMinPrice)

                                .promotion(promoResponse)

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
        }

        @Override
        public ResponseEntity<?> getProductDetail(int id) {
                Product product = productRepository.findDetailById(id)
                                .orElseThrow(
                                                () -> new EntityNotFoundException(
                                                                "Product not found : " + id));

                return ResponseEntity.ok(
                                toDetailResponse(product));
        }

        // Lấy product theo chương trình khuyến mãi
        @Override
        public ResponseEntity<?> getProductOnSale() {
                List<ProductResponse> responses = productRepository.findProductsOnSale(PageRequest.of(0, 6))
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(responses);
        }

}
