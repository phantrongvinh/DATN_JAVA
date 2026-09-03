package com.datn.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.project.dto.preview.CreateReviewRequest;
import com.datn.project.dto.preview.ReviewResponse;
import com.datn.project.dto.preview.ReviewSummaryResponse;
import com.datn.project.entity.Product;
import com.datn.project.entity.ProductReview;
import com.datn.project.entity.ProductReviewId;
import com.datn.project.entity.User;
import com.datn.project.repository.IOrderDetailRepository;
import com.datn.project.repository.IProductRepository;
import com.datn.project.repository.IProductReviewRepository;
import com.datn.project.repository.IUserRepository;

@Service
public class ProductReviewService implements IProductReviewService {

    @Autowired
    private IProductReviewRepository reviewRepository;
    @Autowired
    private IOrderDetailRepository orderDetailRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private ModerationService moderationService;
    @Autowired
    private ProfanityFilterService profanityFilterService;
    @Autowired
    private LoyaltyPointService loyaltyPointService;

    @Override
    public void createReview(Integer userId, Integer productId, CreateReviewRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Đánh giá phải từ 1 đến 5 sao");
        }

        boolean hasPurchased = orderDetailRepository.existsDeliveredPurchase(userId, productId);
        if (!hasPurchased) {
            throw new RuntimeException("Bạn cần mua và nhận sản phẩm này trước khi đánh giá");
        }

        if (reviewRepository.existsById_UserIdAndId_ProductId(userId, productId)) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
        }

        if (profanityFilterService.containsProfanity(request.getComment())) {
            throw new RuntimeException("Nội dung đánh giá chứa từ ngữ không phù hợp, vui lòng chỉnh sửa");
        }

        ModerationService.ModerationResult moderation = moderationService.check(request.getComment());
        if (moderation.flagged()) {
            throw new RuntimeException(
                    "Nội dung đánh giá vi phạm quy định (" + moderation.reason() + "), vui lòng chỉnh sửa");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        ProductReview review = new ProductReview();
        review.setId(new ProductReviewId(userId, productId));
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setVisible(true);

        reviewRepository.save(review);

        loyaltyPointService.awardForReview(user, productId);
    }

    @Override
    public ReviewSummaryResponse getReviews(Integer productId, Integer currentUserId) {
        List<ProductReview> reviews = reviewRepository.findByIdProductIdAndIsVisibleTrueOrderByCreatedAtDesc(productId);

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(r -> ReviewResponse.builder()
                        .userId(r.getUser().getId())
                        .userName(maskName(r.getUser().getFullName()))
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();

        Double avg = reviewRepository.findAverageRating(productId);

        Map<Integer, Long> breakdown = new HashMap<>();
        for (int i = 1; i <= 5; i++)
            breakdown.put(i, 0L);
        reviewRepository.countByRatingGrouped(productId)
                .forEach(row -> breakdown.put((Integer) row[0], (Long) row[1]));

        boolean canReview = currentUserId != null
                && orderDetailRepository.existsDeliveredPurchase(currentUserId, productId)
                && !reviewRepository.existsById_UserIdAndId_ProductId(currentUserId, productId);

        boolean hasReviewed = currentUserId != null
                && reviewRepository.existsById_UserIdAndId_ProductId(currentUserId, productId);

        return ReviewSummaryResponse.builder()
                .averageRating(avg != null ? Math.round(avg * 10) / 10.0 : 0.0)
                .totalReviews(reviews.size())
                .ratingBreakdown(breakdown)
                .reviews(reviewResponses)
                .canReview(canReview)
                .hasReviewed(hasReviewed)
                .build();
    }

    @Override
    public void hideReview(Integer userId, Integer productId) {
        ProductReview review = reviewRepository.findById(new ProductReviewId(userId, productId))
                .orElseThrow(() -> new RuntimeException("Đánh giá không tồn tại"));
        review.setVisible(false);
        reviewRepository.save(review);

    }

    private String maskName(String fullName) {
        if (fullName == null || fullName.isBlank())
            return "Ẩn danh";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length <= 1)
            return parts[0];
        String lastPart = parts[parts.length - 1];
        String masked = lastPart.charAt(0) + "*".repeat(Math.max(0, lastPart.length() - 1));
        parts[parts.length - 1] = masked;
        return String.join(" ", parts);
    }
}
