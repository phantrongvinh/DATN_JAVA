package com.datn.project.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusStatsDTO {
    private String status;
    private Long count;
}
