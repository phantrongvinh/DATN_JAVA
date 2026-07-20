package com.datn.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.dto.product.ProductSummaryDTO;
import com.datn.project.entity.User;
import com.datn.project.repository.IUserRepository;
import com.datn.project.security.CustomUserDetail;
import com.datn.project.service.IWishlistService;

@RestController
@RequestMapping(value = "/api/v1/wishlist")
public class WishlistController {
    @Autowired
    private IWishlistService wishlistService;

    @Autowired
    private IUserRepository userRepository;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();

        String email = customUserDetail.getUsername();

        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        return user.getId();
    }

    @GetMapping("/ids")
    public List<Integer> getIds() {
        return wishlistService.getWishlistProductIds(getCurrentUserId());
    }

    @GetMapping
    public List<ProductSummaryDTO> getWishlist() {
        return wishlistService.getWishlistProducts(getCurrentUserId());
    }

    @PostMapping("/{productId}/toggle")
    public Map<String, Object> toggle(
            @PathVariable Integer productId) {
        boolean added = wishlistService.toggleWishlist(getCurrentUserId(), productId);
        return Map.of("inWishlist", added);
    }
}
