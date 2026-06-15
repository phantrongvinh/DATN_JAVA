package com.datn.project.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.datn.project.dto.ProductFilterDTO;
import com.datn.project.entity.Brand;
import com.datn.project.entity.Category;
import com.datn.project.entity.Product;
import com.datn.project.entity.TargetAudience;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

     public static Specification<Product> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Product> filter(ProductFilterDTO filterDTO) {
        return buildFilter(filterDTO, false);
    }

    public static Specification<Product> adminFilter(ProductFilterDTO filterDTO) {
        return buildFilter(filterDTO, true);
    }

    private static Specification<Product> buildFilter(ProductFilterDTO filterDTO, boolean isAdmin) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Chỉ lấy sản phẩm chưa bị xoá (User)
            if (!isAdmin) {
                predicates.add(cb.isNull(root.get("deletedAt")));
            }

            // Filter audience
            if (filterDTO.getAudienceIds() != null && !filterDTO.getAudienceIds().isEmpty()) {
                Join<Product, TargetAudience> audienceJoin = root.join("targetAudience");
                predicates.add(audienceJoin.get("id").in(filterDTO.getAudienceIds()));
            }

            // Filter brand
            if (filterDTO.getBrandIds() != null && !filterDTO.getBrandIds().isEmpty()) {
                Join<Product, Brand> brandJoin = root.join("brand");
                predicates.add(brandJoin.get("id").in(filterDTO.getBrandIds()));
            }

            // Filter category
            if (filterDTO.getCategoryIds() != null && !filterDTO.getCategoryIds().isEmpty()) {
                Join<Product, Category> categoryJoin = root.join("category");
                predicates.add(categoryJoin.get("id").in(filterDTO.getCategoryIds()));
            }

            // Search theo tên sản phẩm
            if (filterDTO.getSearch() != null && !filterDTO.getSearch().trim().isEmpty()) {
                predicates.add(
                    cb.like(
                        cb.lower(root.get("name")),
                        "%" + filterDTO.getSearch().trim().toLowerCase() + "%"
                    )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
