package com.datn.project.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.datn.project.entity.TimePromotion;

public class TimePromotionSpecification {
    public static Specification<TimePromotion> hasName(String search) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(search)) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
        };
    }

    public static Specification<TimePromotion> isActive(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) return cb.conjunction();
            return cb.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<TimePromotion> build(String search, Boolean isActive) {
        return Specification.where(hasName(search)).and(isActive(isActive));
    }
}
