package com.datn.project.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.datn.project.entity.Brand;
import com.datn.project.entity.Product;
import com.datn.project.entity.TargetAudience;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<Product> filter(List<String> audiences, List<String> brands) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(
                    cb.isNull(root.get("deletedAt")));
            if (audiences != null && !audiences.isEmpty()) {
                Join<Product, TargetAudience> join = root.join("targetAudience");

                predicates.add(
                        cb.lower(join.get("name"))
                                .in(audiences.stream()
                                        .map(String::toLowerCase)
                                        .toList()));
            }
            if (brands != null && !brands.isEmpty()) {
                Join<Product, Brand> join = root.join("brand");

                predicates.add(
                        cb.lower(join.get("name"))
                                .in(brands.stream()
                                        .map(String::toLowerCase)
                                        .toList()));;
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
