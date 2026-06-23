package com.datn.project.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.datn.project.entity.TimePromotion;

public interface ITimePromotionService {

    Optional<TimePromotion> getActiveTimePromotion();

    BigDecimal calcDiscount(BigDecimal orderTotal, TimePromotion promotion);
}
