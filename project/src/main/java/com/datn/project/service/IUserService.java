package com.datn.project.service;

import org.springframework.data.domain.Page;

import com.datn.project.dto.user.UserFilterDTO;
import com.datn.project.dto.user.UserResponse;

public interface IUserService {
    
    Page<UserResponse> getAllUsers(UserFilterDTO filter, int page, int size);
    void softDelete(Integer userId);
    void restore(Integer userId);
}
