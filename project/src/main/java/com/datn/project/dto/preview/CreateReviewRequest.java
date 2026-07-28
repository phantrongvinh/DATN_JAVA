package com.datn.project.dto.preview;

import lombok.Data;

@Data
public class CreateReviewRequest {
  private int rating;
  private String comment;
}
