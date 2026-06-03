package com.datn.project.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.datn.project.entity.Product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<Product> filter(String audience, String brand) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(
                    cb.isNull(root.get("deletedAt")));
            if (audience != null && !audience.isEmpty()) {
                predicates.add(cb.equal(
                        cb.lower(root.join("targetAudience").get("name")), audience.toLowerCase()));
            }
            if (brand != null && !brand.isEmpty()) {
                predicates.add(cb.equal(
                        cb.lower(root.join("brand").get("name")), brand.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
