package com.datn.project.service;

import java.util.List;

import com.datn.project.dto.product.ProductSummaryDTO;

public interface IWishlistService {
    boolean toggleWishlist(Integer userId, Integer productId);

    List<Integer> getWishlistProductIds(Integer userId);

    List<ProductSummaryDTO> getWishlistProducts(Integer userId);
}
