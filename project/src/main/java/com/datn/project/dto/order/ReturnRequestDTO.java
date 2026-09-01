package com.datn.project.dto.order;

import java.util.List;

import lombok.Data;

@Data
public class ReturnRequestDTO {
    private String reason;
    private List<String> images;
}