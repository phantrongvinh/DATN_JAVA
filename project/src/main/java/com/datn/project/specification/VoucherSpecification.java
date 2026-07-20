package com.datn.project.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.datn.project.entity.DiscountType;
import com.datn.project.entity.Voucher;

public class VoucherSpecification  {
     public static Specification<Voucher> hasKeyword(String search) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(search)) return cb.conjunction();
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("code")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Voucher> hasDiscountType(DiscountType discountType) {
        return (root, query, cb) -> {
            if (discountType == null) return cb.conjunction();
            return cb.equal(root.get("discountType"), discountType);
        };
    }

    public static Specification<Voucher> isActive(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) return cb.conjunction();
            return cb.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<Voucher> isStackable(Boolean isStackable) {
        return (root, query, cb) -> {
            if (isStackable == null) return cb.conjunction();
            return cb.equal(root.get("isStackable"), isStackable);
        };
    }

    // Voucher cá nhân (sinh nhật) hay công khai
    public static Specification<Voucher> isPersonal(Boolean isPersonal) {
        return (root, query, cb) -> {
            if (isPersonal == null) return cb.conjunction();
            return isPersonal
                    ? cb.isNotNull(root.get("user"))
                    : cb.isNull(root.get("user"));
        };
    }

    public static Specification<Voucher> build(
            String search, DiscountType discountType, Boolean isActive,
            Boolean isStackable, Boolean isPersonal) {
        return Specification.where(hasKeyword(search))
                .and(hasDiscountType(discountType))
                .and(isActive(isActive))
                .and(isStackable(isStackable))
                .and(isPersonal(isPersonal));
    }
}
