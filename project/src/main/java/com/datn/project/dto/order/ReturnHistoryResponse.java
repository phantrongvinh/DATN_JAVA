package com.datn.project.dto.order;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnHistoryResponse {

    private int id;

    private String status;

    private String reason;

    private String adminNote;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    private String ghnReturnCode;

    private String images;
}
