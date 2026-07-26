package com.datn.project.dto.auth;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private String email;
    private String fullName;
    private String phone;
    private LocalDate birthDay;
    private boolean birthDayEditable;
    private List<String> roles;

    private Integer totalOrders;
    private Integer loyaltyPoints;
    private String memberTier;
    private String nextTierName;
    private BigDecimal amountToNextTier;
}