package com.datn.project.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyOrderDTO {
    private Integer month;
    private Integer year;
    private Integer total;
    private Integer confirmed;
    private Integer shipping;
    private Integer delivered;
    private Integer cancelled;
}
