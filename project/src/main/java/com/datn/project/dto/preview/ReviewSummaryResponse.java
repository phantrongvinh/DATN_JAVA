package com.datn.project.dto.preview;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryResponse {
    private double averageRating;
    private int totalReviews;
    private Map<Integer, Long> ratingBreakdown;
    private List<ReviewResponse> reviews;
    private boolean canReview;
    private boolean hasReviewed;
}