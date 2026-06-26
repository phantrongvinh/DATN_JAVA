package com.datn.project.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.datn.project.dto.PromotionResponse;
import com.datn.project.dto.product.ProductDetailDTO;
import com.datn.project.dto.product.ProductFilterDTO;
import com.datn.project.dto.product.ProductHomeView;
import com.datn.project.dto.product.ProductImageDTO;
import com.datn.project.dto.product.ProductImageRequest;
import com.datn.project.dto.product.ProductOverview;
import com.datn.project.dto.product.ProductRequest;
import com.datn.project.dto.product.ProductResponse;
import com.datn.project.dto.product.ProductVariantDTO;
import com.datn.project.dto.product.ProductVariantRequest;
import com.datn.project.dto.product.ProductVariantResponse;
import com.datn.project.entity.Product;
import com.datn.project.entity.ProductImage;
import com.datn.project.entity.ProductVariant;
import com.datn.project.entity.Promotion;
import com.datn.project.entity.Size;
import com.datn.project.repository.IBrandRepository;
import com.datn.project.repository.ICategoryRepository;
import com.datn.project.repository.IProductImageRepository;
import com.datn.project.repository.IProductRepository;
import com.datn.project.repository.IProductVariantRepository;
import com.datn.project.repository.ISizeRepository;
import com.datn.project.repository.ITargetAudienceRepository;
import com.datn.project.specification.ProductSpecification;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ProductService implements IProductService {

        @Autowired
        private IProductRepository productRepository;

        @Autowired
        private IBrandRepository brandRepository;

        @Autowired
        private ICategoryRepository categoryRepository;

        @Autowired
        private IPromotionService promotionService;

        @Autowired
        private IProductVariantRepository productVariantRepository;

        @Autowired
        private IProductImageRepository productImageRepository;

        @Autowired
        private ITargetAudienceRepository targetAudienceRepository;

        @Autowired
        private ISizeRepository sizeRepository;

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

        // private ProductVariantResponse toVariantResponse(
        // ProductVariant variant,
        // Optional<Promotion> promotion) {

        // ProductVariantResponse response = new ProductVariantResponse();

        // response.setId(variant.getId());
        // response.setColor(variant.getColor());
        // response.setSize(variant.getSize().getName());
        // response.setStock(variant.getStock());
        // response.setSku(variant.getSku());
        // response.setPrice(variant.getPrice());

        // response.setDiscountedPrice(
        // promotion
        // .map(pr -> promotionService.calcDiscountedPrice(
        // variant.getPrice(), pr))
        // .orElse(variant.getPrice()));

        // response.setCreatedAt(variant.getCreatedAt());

        // return response;
        // }

        // private ProductResponse toResponse(Product p) {

        // ProductResponse response = new ProductResponse();

        // response.setId(p.getId());
        // response.setName(p.getName());
        // response.setDescription(p.getDescription());
        // response.setBasePrice(p.getBasePrice());
        // response.setCreatedAt(p.getCreatedAt());

        // response.setCategory(p.getCategory().getName());
        // response.setBrand(p.getBrand().getName());
        // response.setTargetAudience(p.getTargetAudience().getName());
        // response.setAccessory(p.getCategory().isAccessory());

        // response.setImg(
        // p.getProductImages()
        // .stream()
        // .findFirst()
        // .map(ProductImage::getImageUrl)
        // .orElse(null));

        // BigDecimal minPrice = p.getProductVariants()
        // .stream()
        // .map(ProductVariant::getPrice)
        // .min(BigDecimal::compareTo)
        // .orElse(p.getBasePrice());

        // BigDecimal maxPrice = p.getProductVariants()
        // .stream()
        // .map(ProductVariant::getPrice)
        // .max(BigDecimal::compareTo)
        // .orElse(p.getBasePrice());

        // Optional<Promotion> promo = getBestPromotion(p, minPrice);

        // response.setMinPrice(minPrice);
        // response.setMaxPrice(maxPrice);

        // response.setDiscountedPrice(
        // promo.map(pr -> promotionService.calcDiscountedPrice(minPrice, pr))
        // .orElse(minPrice));

        // promo.ifPresent(pr -> response.setPromotion(toPromotionResponse(pr)));

        // List<ProductVariantResponse> variants = p.getProductVariants()
        // .stream()
        // .map(v -> toVariantResponse(v, promo))
        // .toList();

        // response.setProductVariant(variants);

        // return response;
        // }

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
                Page<Product> products = productRepository.findAll(ProductSpecification.filter(filterDTO), pageable);

                if (products.isEmpty()) {
                        return ResponseEntity.ok(
                                        Map.of("message", "Không tìm thấy sản phẩm"));
                }

                List<ProductHomeView> responseList = products.stream()
                                .map(product -> {
                                        ProductHomeView res = new ProductHomeView();
                                        res.setCategoryName(product.getCategory().getName());
                                        res.setId(product.getId());
                                        res.setName(product.getName());
                                        res.setBrandName(product.getBrand().getName());

                                        ProductImage productImage = product.getProductImages().stream()
                                                        .filter(pi -> Boolean.TRUE.equals(pi.getIsPrimary()))
                                                        .findFirst()
                                                        .orElse(null);
                                        res.setImage(productImage != null ? productImage.getImageUrl() : null);
                                        // Min và Max price
                                        BigDecimal minPrice = product.getProductVariants()
                                                        .stream()
                                                        .map(ProductVariant::getPrice)
                                                        .min(BigDecimal::compareTo)
                                                        .orElse(product.getBasePrice());

                                        // Promotion tốt nhất
                                        Optional<Promotion> promo = getBestPromotion(product, minPrice);
                                        PromotionResponse promoResponse = promo
                                                        .map(this::toPromotionResponse)
                                                        .orElse(null);

                                        BigDecimal discountedMinPrice = promo
                                                        .map(pr -> promotionService.calcDiscountedPrice(minPrice, pr))
                                                        .orElse(minPrice);

                                        res.setPrice(minPrice);
                                        res.setDiscountPrice(discountedMinPrice);
                                        res.setPromotion(promoResponse);
                                        return res;
                                })
                                .toList();
                Slice<ProductHomeView> response = new SliceImpl<>(responseList, pageable, products.hasNext());

                return ResponseEntity.ok(Map.of(
                                "content", responseList,
                                "hasNext", products.hasNext(),
                                "page", page,
                                "size", size,
                                "totalElements", products.getTotalElements(),
                                "totalPages", products.getTotalPages()));
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
                Page<Product> products = productRepository.findAll(
                                ProductSpecification.adminFilter(filterDTO), pageable);

                if (products.isEmpty()) {
                        return ResponseEntity.ok(Map.of("message", "Không tìm thấy sản phẩm"));
                }

                Page<ProductResponse> responses = products.map(p -> {
                        ProductResponse res = new ProductResponse();

                        // ─── Thông tin cơ bản ────────────────────────────
                        res.setId(p.getId());
                        res.setName(p.getName());
                        res.setDescription(p.getDescription());
                        res.setBasePrice(p.getBasePrice());
                        res.setCreatedAt(p.getCreatedAt());
                        res.setCategory(p.getCategory().getName());
                        res.setBrand(p.getBrand().getName());
                        res.setTargetAudience(p.getTargetAudience().getName());
                        res.setAccessory(p.getCategory().isAccessory());
                        res.setDeletedAt(p.getDeletedAt());

                        // ─── Ảnh primary ─────────────────────────────────
                        res.setImg(p.getProductImages().stream()
                                        .filter(pi -> Boolean.TRUE.equals(pi.getIsPrimary()))
                                        .findFirst()
                                        .map(ProductImage::getImageUrl)
                                        .orElse(p.getProductImages().stream()
                                                        .findFirst()
                                                        .map(ProductImage::getImageUrl)
                                                        .orElse(null)));

                        // ─── Min / Max price từ variants ─────────────────
                        List<BigDecimal> prices = p.getProductVariants().stream()
                                        .map(ProductVariant::getPrice)
                                        .toList();

                        BigDecimal minPrice = prices.isEmpty()
                                        ? p.getBasePrice()
                                        : Collections.min(prices);

                        BigDecimal maxPrice = prices.isEmpty()
                                        ? p.getBasePrice()
                                        : Collections.max(prices);

                        // ─── Promotion ───────────────────────────────────
                        Optional<Promotion> promo = getBestPromotion(p, minPrice);
                        Promotion activePromo = promo.orElse(null);

                        res.setMinPrice(minPrice);
                        res.setMaxPrice(maxPrice);
                        res.setDiscountedPrice(promo
                                        .map(pr -> promotionService.calcDiscountedPrice(minPrice, pr))
                                        .orElse(minPrice));
                        res.setPromotion(promo.map(this::toPromotionResponse).orElse(null));

                        // ─── Variants ────────────────────────────────────
                        List<ProductVariantResponse> variantResponses = p.getProductVariants().stream()
                                        .map(v -> {
                                                ProductVariantResponse vRes = new ProductVariantResponse();
                                                vRes.setId(v.getId());
                                                vRes.setColor(v.getColor());
                                                vRes.setSize(v.getSize().getName());
                                                vRes.setStock(v.getStock());
                                                vRes.setSku(v.getSku());
                                                vRes.setPrice(v.getPrice());
                                                vRes.setDiscountedPrice(activePromo != null
                                                                ? promotionService.calcDiscountedPrice(v.getPrice(),
                                                                                activePromo)
                                                                : v.getPrice());
                                                vRes.setCreatedAt(v.getCreatedAt());
                                                return vRes;
                                        })
                                        .toList();

                        res.setProductVariant(variantResponses);

                        return res;
                });

                return ResponseEntity.ok(responses);

        }

        // Tạo mới product ───────────────────────────────
        @Override
        public ResponseEntity<?> createProduct(ProductRequest request) {
                Product product = new Product();
                setProductFields(product, request);
                Product saved = productRepository.save(product);

                saveVariants(saved, request.getVariants());
                saveImages(saved, request.getImages());

                return getProductDetail(saved.getId());
        }

        // ─── Helper: set fields ───────────────────────────────
        private void setProductFields(Product product, ProductRequest request) {
                product.setName(request.getName());
                product.setDescription(request.getDescription());
                product.setBasePrice(request.getBasePrice());
                product.setCategory(categoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Category không tồn tại")));
                product.setBrand(brandRepository.findById(request.getBrandId())
                                .orElseThrow(() -> new RuntimeException("Brand không tồn tại")));
                product.setTargetAudience(targetAudienceRepository.findById(request.getTargetAudienceId())
                                .orElseThrow(() -> new RuntimeException("TargetAudience không tồn tại")));
        }

        // ─── Helper: save variants (tạo mới) ─────────────────
        private void saveVariants(Product product, List<ProductVariantRequest> requests) {
                if (requests == null)
                        return;
                requests.forEach(req -> {
                        ProductVariant variant = new ProductVariant();
                        setVariantFields(variant, product, req);
                        productVariantRepository.save(variant);
                });
        }

        // ─── Helper: update variants ──────────────────────────
        private void updateVariants(Product product, List<ProductVariantRequest> requests) {
                if (requests == null)
                        return;

                List<Integer> keepIds = new ArrayList<>();

                requests.forEach(req -> {
                        ProductVariant variant = req.getId() != null
                                        ? productVariantRepository.findById(req.getId())
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Variant không tồn tại"))
                                        : new ProductVariant();

                        setVariantFields(variant, product, req);
                        ProductVariant saved = productVariantRepository.save(variant);
                        keepIds.add(saved.getId());
                });

                // xóa variant không còn trong danh sách
                if (!keepIds.isEmpty()) {
                        productVariantRepository.deleteByProductIdAndIdNotIn(product.getId(), keepIds);
                }
        }

        private void setVariantFields(ProductVariant variant, Product product, ProductVariantRequest req) {
                Size size = sizeRepository.findById(req.getSizeId())
                                .orElseThrow(() -> new RuntimeException("Size không tồn tại"));
                variant.setProduct(product);
                variant.setColor(req.getColor());
                variant.setSize(size);
                variant.setStock(req.getStock());
                variant.setPrice(req.getPrice());
                variant.setSku(req.getSku());
        }

        // ─── Helper: save images (tạo mới) ───────────────────
        private void saveImages(Product product, List<ProductImageRequest> requests) {
                if (requests == null)
                        return;
                // đảm bảo chỉ 1 ảnh primary
                boolean hasPrimary = requests.stream().anyMatch(r -> Boolean.TRUE.equals(r.getIsPrimary()));
                requests.forEach(req -> {
                        ProductImage image = new ProductImage();
                        image.setProduct(product);
                        image.setImageUrl(req.getImageUrl());
                        image.setIsPrimary(!hasPrimary
                                        ? requests.indexOf(req) == 0 // ảnh đầu tiên làm primary nếu không set
                                        : Boolean.TRUE.equals(req.getIsPrimary()));
                        productImageRepository.save(image);
                });
        }

        // ─── Helper: update images ────────────────────────────
        private void updateImages(Product product, List<ProductImageRequest> requests) {
                if (requests == null)
                        return;

                List<Integer> keepIds = new ArrayList<>();

                requests.forEach(req -> {
                        ProductImage image = req.getId() != null
                                        ? productImageRepository.findById(req.getId())
                                                        .orElseThrow(() -> new RuntimeException("Image không tồn tại"))
                                        : new ProductImage();

                        image.setProduct(product);
                        image.setImageUrl(req.getImageUrl());
                        image.setIsPrimary(Boolean.TRUE.equals(req.getIsPrimary()));
                        ProductImage saved = productImageRepository.save(image);
                        keepIds.add(saved.getId());
                });

                if (!keepIds.isEmpty()) {
                        productImageRepository.deleteByProductIdAndIdNotIn(product.getId(), keepIds);
                }
        }

        // Vô hiệu hóa và khôi phục product theo id
        @Override
        @Transactional
        public ResponseEntity<?> deactivateProduct(Integer id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product không tồn tại"));

                if (product.getDeletedAt() != null) {
                        productRepository.softDelete(id, null);
                } else {
                        productRepository.softDelete(id, LocalDateTime.now());
                }

                return ResponseEntity.ok("Câph nhật thành công");
        }

        // Cập nhật product theo id
        @Override
        @Transactional
        public ResponseEntity<?> updateProduct(Integer id, ProductRequest request) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product không tồn tại"));

                setProductFields(product, request);
                productRepository.save(product);

                updateVariants(product, request.getVariants());
                updateImages(product, request.getImages());

                return getProductDetail(id);
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

        @Override
        public ResponseEntity<?> getSpotlightProducts() {
                List<ProductHomeView> responses = productRepository.findTop4ByDeletedAtIsNullOrderByCreatedAtDesc()
                                .stream()
                                .map(product -> {
                                        ProductHomeView res = new ProductHomeView();
                                        res.setCategoryName(product.getCategory().getName());
                                        res.setId(product.getId());
                                        res.setName(product.getName());
                                        res.setBrandName(product.getBrand().getName());

                                        ProductImage productImage = product.getProductImages().stream()
                                                        .filter(pi -> Boolean.TRUE.equals(pi.getIsPrimary()))
                                                        .findFirst()
                                                        .orElse(null);
                                        res.setImage(productImage != null ? productImage.getImageUrl() : null);
                                        // Min và Max price
                                        BigDecimal minPrice = product.getProductVariants()
                                                        .stream()
                                                        .map(ProductVariant::getPrice)
                                                        .min(BigDecimal::compareTo)
                                                        .orElse(product.getBasePrice());

                                        // Promotion tốt nhất
                                        Optional<Promotion> promo = getBestPromotion(product, minPrice);
                                        PromotionResponse promoResponse = promo
                                                        .map(this::toPromotionResponse)
                                                        .orElse(null);

                                        BigDecimal discountedMinPrice = promo
                                                        .map(pr -> promotionService.calcDiscountedPrice(minPrice, pr))
                                                        .orElse(minPrice);

                                        res.setPrice(minPrice);
                                        res.setDiscountPrice(discountedMinPrice);
                                        res.setPromotion(promoResponse);
                                        return res;
                                })
                                .toList();
                return ResponseEntity.ok(responses);

        }

        // Lấy product theo chương trình khuyến mãi
        @Override
        public ResponseEntity<?> getProductOnSale() {

                List<ProductHomeView> responses = productRepository.findProductsOnSale(PageRequest.of(0, 4))
                                .stream()
                                .map(product -> {
                                        ProductHomeView res = new ProductHomeView();
                                        res.setCategoryName(product.getCategory().getName());
                                        res.setId(product.getId());
                                        res.setName(product.getName());
                                        res.setBrandName(product.getBrand().getName());

                                        ProductImage productImage = product.getProductImages().stream()
                                                        .filter(pi -> Boolean.TRUE.equals(pi.getIsPrimary()))
                                                        .findFirst()
                                                        .orElse(null);
                                        res.setImage(productImage != null ? productImage.getImageUrl() : null);

                                        // Min và Max price
                                        BigDecimal minPrice = product.getProductVariants()
                                                        .stream()
                                                        .map(ProductVariant::getPrice)
                                                        .min(BigDecimal::compareTo)
                                                        .orElse(product.getBasePrice());

                                        // Promotion tốt nhất
                                        Optional<Promotion> promo = getBestPromotion(product, minPrice);
                                        PromotionResponse promoResponse = promo
                                                        .map(this::toPromotionResponse)
                                                        .orElse(null);
                                        BigDecimal discountedMinPrice = promo
                                                        .map(pr -> promotionService.calcDiscountedPrice(minPrice, pr))
                                                        .orElse(minPrice);

                                        res.setPrice(minPrice);
                                        res.setDiscountPrice(discountedMinPrice);
                                        res.setPromotion(promoResponse);
                                        return res;
                                })
                                .toList();

                return ResponseEntity.ok(responses);
        }

}
