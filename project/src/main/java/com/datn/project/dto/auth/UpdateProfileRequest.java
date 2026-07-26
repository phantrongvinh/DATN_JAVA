package com.datn.project.dto.auth;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private LocalDate birthDay;
}
