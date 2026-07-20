package com.datn.project.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.datn.project.entity.DiscountType;
import com.datn.project.entity.Promotion;

public class PromotionSpecification {
    public static Specification<Promotion> hasName(String search) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(search)) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
        };
    }

    public static Specification<Promotion> hasDiscountType(DiscountType discountType) {
        return (root, query, cb) -> {
            if (discountType == null) return cb.conjunction();
            return cb.equal(root.get("discountType"), discountType);
        };
    }

    // Đang diễn ra: now nằm giữa startAt và endAt
    public static Specification<Promotion> isCurrentlyActive() {
        return (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();
            return cb.and(
                    cb.lessThanOrEqualTo(root.get("startAt"), now),
                    cb.greaterThanOrEqualTo(root.get("endAt"), now)
            );
        };
    }

    // Sắp diễn ra: startAt trong tương lai
    public static Specification<Promotion> isUpcoming() {
        return (root, query, cb) -> cb.greaterThan(root.get("startAt"), LocalDateTime.now());
    }

    // Đã kết thúc: endAt trong quá khứ
    public static Specification<Promotion> isEnded() {
        return (root, query, cb) -> cb.lessThan(root.get("endAt"), LocalDateTime.now());
    }

    public static Specification<Promotion> build(String search, DiscountType discountType, String status) {
        Specification<Promotion> spec = Specification.where(hasName(search))
                .and(hasDiscountType(discountType));

        if ("ACTIVE".equalsIgnoreCase(status)) {
            spec = spec.and(isCurrentlyActive());
        } else if ("UPCOMING".equalsIgnoreCase(status)) {
            spec = spec.and(isUpcoming());
        } else if ("ENDED".equalsIgnoreCase(status)) {
            spec = spec.and(isEnded());
        }

        return spec;
    }
}
