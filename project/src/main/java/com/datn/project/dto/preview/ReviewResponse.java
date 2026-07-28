package com.datn.project.dto.preview;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Integer userId;
    private String userName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
