package com.datn.project.service;

import com.datn.project.dto.preview.CreateReviewRequest;
import com.datn.project.dto.preview.ReviewSummaryResponse;

public interface IProductReviewService {
    ReviewSummaryResponse getReviews(Integer productId, Integer currentUserId);

    void createReview(Integer userId, Integer productId, CreateReviewRequest request);

    void hideReview(Integer userId, Integer productId);
}
