package com.datn.project.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface IReturnRequestService {
    void requestReturn(Integer userId, Integer orderId, String reason, List<MultipartFile> images);

    void resolveReturn(Integer returnRequestId, boolean approved, String adminNote);

    void completeReturn(Integer returnRequestId);

    void manualCompleteReturn(Integer returnRequestId);
}
