package com.datn.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.project.entity.DiscountType;
import com.datn.project.entity.TimePromotion;
import com.datn.project.repository.ITimePromotionRepository;

@Service
public class TimePromotionService implements ITimePromotionService {

    @Autowired
    private ITimePromotionRepository timePromotionRepository;

    @Override
    public Optional<TimePromotion> getActiveTimePromotion() {
        LocalTime now = LocalTime.now();
        return timePromotionRepository.findActiveByTime(now);
    }

    @Override
    public BigDecimal calcDiscount(BigDecimal orderTotal, TimePromotion promotion) {
        if (promotion.getDiscountType() == DiscountType.PERCENT) {
            return orderTotal.multiply(
                    promotion.getDiscountValue().divide(BigDecimal.valueOf(100))).setScale(0, RoundingMode.HALF_UP);
        }
        return promotion.getDiscountValue().min(orderTotal);
    }
}
