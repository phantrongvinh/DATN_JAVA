package com.datn.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.datn.project.dto.PromotionRequest;
import com.datn.project.dto.PromotionResponse;
import com.datn.project.entity.DiscountType;
import com.datn.project.entity.Product;
import com.datn.project.entity.Promotion;
import com.datn.project.repository.IProductRepository;
import com.datn.project.repository.IPromotionRepository;
import com.datn.project.specification.PromotionSpecification;

@Service
public class PromotionService implements IPromotionService {

    @Autowired
    private IPromotionRepository promotionRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    public ResponseEntity<?> createPromotion(PromotionRequest req) {
        List<Product> products = productRepository.findAllById(req.getProductIds());

        Promotion promotion = new Promotion();
        promotion.setName(req.getName());
        promotion.setDiscountType(req.getDiscountType());
        promotion.setDiscountValue(req.getDiscountValue());
        promotion.setStartAt(req.getStartAt());
        promotion.setEndAt(req.getEndAt());
        promotion.setProducts(products);
        promotionRepository.save(promotion);
        return ResponseEntity.ok("Tạo khuyến mãi thành công");
    }

    @Override
    public Optional<Promotion> getActivePromotion(Integer productId) {
        return promotionRepository.findActiveByProductId(productId, LocalDateTime.now());
    }

    @Override
    public ResponseEntity<?> getAllActivePromotioEntity() {

        List<PromotionResponse> responses = promotionRepository.findActivePromotion(LocalDateTime.now()).stream()
                .map(p -> {
                    PromotionResponse res = new PromotionResponse();

                    res.setId(p.getId());
                    res.setName(p.getName());
                    res.setDiscountType(p.getDiscountType().name());
                    res.setDiscountValue(p.getDiscountValue());
                    res.setStartAt(p.getStartAt());
                    res.setEndAt(p.getEndAt());

                    return res;
                }).toList();

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<?> getAllPromotion(int page, int size, String search, DiscountType discountType,
            String status) {
        Specification<Promotion> spec = PromotionSpecification.build(search, discountType, status);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        Page<Promotion> promotionPage = promotionRepository.findAll(spec, pageable);

        List<Integer> promotionIds = promotionPage.getContent().stream()
                .map(Promotion::getId)
                .toList();

        Map<Integer, List<String>> productNamesByPromotion = new HashMap<>();
        if (!promotionIds.isEmpty()) {
            for (Object[] row : promotionRepository.findProductNamesByPromotionIds(promotionIds)) {
                Integer promotionId = (Integer) row[0];
                String productName = (String) row[1];
                productNamesByPromotion
                        .computeIfAbsent(promotionId, k -> new ArrayList<>())
                        .add(productName);
            }
        }

        Page<PromotionResponse> resPage = promotionPage.map(p -> {
            PromotionResponse res = new PromotionResponse();
            res.setId(p.getId());
            res.setName(p.getName());
            res.setDiscountType(p.getDiscountType().name());
            res.setDiscountValue(p.getDiscountValue());
            res.setStartAt(p.getStartAt());
            res.setEndAt(p.getEndAt());

            List<String> names = productNamesByPromotion.getOrDefault(p.getId(), List.of());
            res.setProductNames(names);
            res.setProductCount((long) names.size());

            return res;
        });

        return ResponseEntity.ok(resPage);
    }

    @Override
    public BigDecimal calcDiscountedPrice(BigDecimal originalPrice, Promotion promotion) {
        if (promotion.getDiscountType() == DiscountType.PERCENT) {
            BigDecimal multiplier = BigDecimal.ONE
                    .subtract(promotion.getDiscountValue().divide(BigDecimal.valueOf(100)));
            return originalPrice.multiply(multiplier).setScale(0, RoundingMode.HALF_UP);
        }
        return originalPrice.subtract(promotion.getDiscountValue()).max(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getDiscountAmount(BigDecimal price, Promotion promotion) {

        if (promotion.getDiscountType() == DiscountType.PERCENT) {
            return price.multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        }

        if (promotion.getDiscountType() == DiscountType.FIXED) {
            return promotion.getDiscountValue();
        }

        return BigDecimal.ZERO;
    }

}
