package com.datn.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.project.dto.product.ProductResponse;
import com.datn.project.dto.product.ProductSummaryDTO;
import com.datn.project.entity.Product;
import com.datn.project.entity.ProductImage;
import com.datn.project.entity.User;
import com.datn.project.entity.Wishlist;
import com.datn.project.entity.WishlistId;
import com.datn.project.repository.IProductRepository;
import com.datn.project.repository.IUserRepository;
import com.datn.project.repository.IWishlistRepository;

@Service
public class WishlistService implements IWishlistService {

    @Autowired
    private IWishlistRepository wishlistRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IUserRepository userRepository;

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
        if (ids.isEmpty()) return List.of();

        List<Product> products = productRepository.findAllWithImagesByIds(ids);

        return products.stream()
                .map(p -> ProductSummaryDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .image(resolveImage(p))
                        .price(p.getBasePrice())
                        .build())
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
}
