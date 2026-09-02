package com.datn.project.dto.order;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnResponse {

    private int id;
    private int orderId;
    private String receiverName;
    private String reason;
    private List<String> images;
    private String status;
    private String adminNote;
    private LocalDateTime createdAt;
    private String fromPhone;
    private String toPhone;
    private String address;
}