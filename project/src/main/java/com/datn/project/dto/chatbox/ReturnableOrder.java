package com.datn.project.dto.chatbox;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReturnableOrder {
    private Integer orderId;
    private LocalDateTime deliveredAt;
    private BigDecimal finalPrice;
    private List<String> productNames;
}