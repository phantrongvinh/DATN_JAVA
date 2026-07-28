package com.datn.project.service;

import org.springframework.http.ResponseEntity;

import com.datn.project.dto.auth.LoginRequest;
import com.datn.project.dto.auth.RegisterRequest;
import com.datn.project.dto.auth.UpdateProfileRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {

    ResponseEntity<?> register(RegisterRequest request);

    ResponseEntity<?> login(LoginRequest request);

    ResponseEntity<?> logout(HttpServletRequest request);

    void activate(String token);

    void resendActivation(String email);

    ResponseEntity<?> me();

    ResponseEntity<?> updateProfile(UpdateProfileRequest request) ;

    ResponseEntity<?> forgotPassword(String email);

    ResponseEntity<?> resetPassword(String token, String newPassword);
}
