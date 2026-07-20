package com.datn.project.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.datn.project.dto.dashboard.DashboardResponse;

public interface IDashboardService {
    DashboardResponse getDashboard(LocalDate start, LocalDate end, Integer chartYear);

    double calcGrowth(BigDecimal current, BigDecimal previous);
}
