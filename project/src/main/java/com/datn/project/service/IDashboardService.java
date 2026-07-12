package com.datn.project.service;

import java.math.BigDecimal;

import com.datn.project.dto.dashboard.DashboardResponse;

public interface IDashboardService {
    DashboardResponse getDashboard(Integer month, Integer year);

    double calcGrowth(BigDecimal current, BigDecimal previous);
}
