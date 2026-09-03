package com.datn.project.service;

import com.datn.project.entity.OrderDetail;
import com.datn.project.entity.User;
import com.datn.project.repository.IOrderDetailRepository;
import com.datn.project.repository.IProductReviewRepository;
import com.datn.project.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class LoyaltyPointService {

    @Autowired
    private IOrderDetailRepository orderDetailRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IProductReviewRepository reviewRepository;

    private int calcPoints(OrderDetail detail) {
        return detail.getPrice()
                .multiply(BigDecimal.valueOf(detail.getQuantity()))
                .divide(BigDecimal.valueOf(10_000), RoundingMode.DOWN)
                .intValue();
    }

    @Transactional
    public int awardPoints(List<OrderDetail> details, User user) {
        int total = 0;
        for (OrderDetail d : details) {
            if (d.isPointsAwarded())
                continue;
            total += calcPoints(d);
            d.setPointsAwarded(true);
            orderDetailRepository.save(d);
        }
        if (total > 0) {
            user.setLoyaltyPoints(user.getLoyaltyPoints() + total);
            userRepository.save(user);
        }
        return total;
    }

    @Transactional
    public int awardForReview(User user, Integer productId) {
        List<OrderDetail> eligible = orderDetailRepository
                .findUnrewardedDeliveredDetails(user.getId(), productId);
        return awardPoints(eligible, user);
    }

    @Transactional
    public int awardForDeliveredOrder(User user, List<OrderDetail> orderDetails) {
        List<OrderDetail> autoRewardable = orderDetails.stream()
                .filter(d -> !d.isPointsAwarded())
                .filter(d -> reviewRepository.existsById_UserIdAndId_ProductId(
                        user.getId(), d.getProductVariant().getProduct().getId()))
                .toList();
        return awardPoints(autoRewardable, user);
    }

    @Transactional
    public int deductForReturnedOrder(User user, List<OrderDetail> details) {
        int totalDeduct = 0;
        for (OrderDetail d : details) {
            if (!d.isPointsAwarded())
                continue;
            totalDeduct += calcPoints(d);
            d.setPointsAwarded(false); 
            orderDetailRepository.save(d);
        }
        if (totalDeduct > 0) {
            user.setLoyaltyPoints(Math.max(0, user.getLoyaltyPoints() - totalDeduct));
            userRepository.save(user);
        }
        return totalDeduct;
    }
}