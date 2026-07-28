package com.datn.project.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.project.dto.PromotionResponse;
import com.datn.project.dto.product.ProductSummaryDTO;
import com.datn.project.entity.Product;
import com.datn.project.entity.ProductImage;
import com.datn.project.entity.ProductVariant;
import com.datn.project.entity.Promotion;
import com.datn.project.entity.User;
import com.datn.project.entity.Wishlist;
import com.datn.project.entity.WishlistId;
import com.datn.project.repository.IProductRepository;
import com.datn.project.repository.IUserRepository;
import com.datn.project.repository.IWishlistRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WishlistService implements IWishlistService {

        @Autowired
        private IWishlistRepository wishlistRepository;

        @Autowired
        private IProductRepository productRepository;

        @Autowired
        private IUserRepository userRepository;

        @Autowired
        private IPromotionService promotionService;

        @Override
        public boolean toggleWishlist(Integer userId, Integer productId) {
                boolean exists = wishlistRepository.existsById_UserIdAndId_ProductId(userId, productId);

                if (exists) {
                        wishlistRepository.deleteById_UserIdAndId_ProductId(userId, productId);
                        return false;
                }

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

                Wishlist w = Wishlist.builder()
                                .id(new WishlistId(userId, productId))
                                .user(user)
                                .product(product)
                                .build();
                wishlistRepository.save(w);
                return true;
        }

        @Override
        public List<Integer> getWishlistProductIds(Integer userId) {
                return wishlistRepository.findProductIdsByUserId(userId);
        }

        @Override
        public List<ProductSummaryDTO> getWishlistProducts(Integer userId) {
                List<Integer> ids = getWishlistProductIds(userId);

                if (ids.isEmpty()) {
                        return List.of();
                }

                List<Product> products = productRepository.findAllWithImagesByIds(ids);

                return products.stream()
                                .map(product -> {
                                        BigDecimal minPrice = product.getProductVariants().stream()
                                                        .map(ProductVariant::getPrice)
                                                        .min(BigDecimal::compareTo)
                                                        .orElse(product.getBasePrice());

                                        // Promotion tốt nhất
                                        Optional<Promotion> promo = getBestPromotion(product, minPrice);

                                        BigDecimal discountPrice = promo
                                                        .map(pr -> promotionService.calcDiscountedPrice(minPrice,pr) )
                                                        .orElse(minPrice);

                                        return ProductSummaryDTO.builder()
                                                        .id(product.getId())
                                                        .name(product.getName())
                                                        .image(resolveImage(product))
                                                        .price(minPrice)
                                                        .discountPrice(discountPrice)
                                                        .promotion(promo.map(this::toPromotionResponse).orElse(null))
                                                        .build();
                                })
                                .toList();
        }

        private String resolveImage(Product p) {
                return p.getProductImages().stream()
                                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                                .findFirst()
                                .map(ProductImage::getImageUrl)
                                .orElseGet(() -> p.getProductImages().stream()
                                                .findFirst()
                                                .map(ProductImage::getImageUrl)
                                                .orElse(null));
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

         private Optional<Promotion> getBestPromotion(Product product, BigDecimal price) {

                LocalDateTime now = LocalDateTime.now();

                return product.getPromotions()
                                .stream()
                                .filter(pr -> !pr.getStartAt().isAfter(now)
                                                && !pr.getEndAt().isBefore(now))
                                .min(Comparator.comparing(
                                                pr -> promotionService.calcDiscountedPrice(price, pr)));
        }
}
