package com.datn.project.service;

import java.util.List;

public interface IReturnRequestService {
    void requestReturn(Integer userId, Integer orderId, String reason, List<String> imageUrls);

    void resolveReturn(Integer returnRequestId, boolean approved, String adminNote);
}
