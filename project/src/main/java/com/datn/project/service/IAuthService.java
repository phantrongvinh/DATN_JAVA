package com.datn.project.service;

import org.springframework.http.ResponseEntity;

import com.datn.project.dto.LoginRequest;
import com.datn.project.dto.RegisterRequest;

public interface IAuthService {

    ResponseEntity<?> register(RegisterRequest request);

    ResponseEntity<?> login(LoginRequest request);
}
