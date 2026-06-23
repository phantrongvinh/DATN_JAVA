package com.datn.project.dto.auth;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String phone;
    private LocalDate birthDay;

}
