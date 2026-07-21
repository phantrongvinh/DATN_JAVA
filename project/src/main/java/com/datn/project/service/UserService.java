package com.datn.project.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.datn.project.dto.user.OrderSummaryResponse;
import com.datn.project.dto.user.UserFilterDTO;
import com.datn.project.dto.user.UserResponse;
import com.datn.project.entity.OrderStatus;
import com.datn.project.entity.User;
import com.datn.project.repository.IOrderRepository;
import com.datn.project.repository.IUserRepository;
import com.datn.project.specification.UserSpecification;

@Service
public class UserService implements IUserService {

        @Autowired
        private IUserRepository userRepository;

        @Autowired
        private IOrderRepository orderRepository;

        @Override
        public Page<UserResponse> getAllUsers(UserFilterDTO filter, int page, int size) {
                Sort sort = switch (filter.getSortBy() == null ? "" : filter.getSortBy()) {
                        case "oldest" -> Sort.by("createdAt").ascending();
                        case "spending_desc" -> Sort.by("totalSpending").descending();
                        case "spending_asc" -> Sort.by("totalSpending").ascending();
                        default -> Sort.by("createdAt").descending();
                };

                Pageable pageable = PageRequest.of(page - 1, size, sort);
                Page<User> users = userRepository.findAll(
                                UserSpecification.adminFilter(filter), pageable);

                // ─── Batch query stats ────────────────────────────
                List<Integer> userIds = users.getContent().stream()
                                .map(User::getId)
                                .toList();

                List<Object[]> stats = userRepository.findOrderStatsByUserIds(userIds);
                Map<Integer, Object[]> statsMap = stats.stream()
                                .collect(Collectors.toMap(
                                                row -> (Integer) row[0],
                                                row -> row));

                // ─── Batch query delivered orders ────────────────
                List<Object[]> deliveredOrders = orderRepository.findDeliveredOrdersByUserIds(userIds);
                Map<Integer, List<OrderSummaryResponse>> ordersMap = new HashMap<>();

                deliveredOrders.forEach(row -> {
                        Integer userId = (Integer) row[0];
                        Integer orderId = (Integer) row[1];
                        BigDecimal price = (BigDecimal) row[2];
                        OrderStatus status = (OrderStatus) row[3];
                        LocalDateTime date = (LocalDateTime) row[4];

                        ordersMap.computeIfAbsent(userId, k -> new ArrayList<>())
                                        .add(OrderSummaryResponse.builder()
                                                        .id(orderId)
                                                        .finalPrice(price)
                                                        .status(status.name())
                                                        .createdAt(date)
                                                        .build());
                });

                // ─── Map sang response ────────────────────────────
                List<UserResponse> responses = users.getContent().stream()
                                .map(user -> {
                                        Object[] stat = statsMap.getOrDefault(
                                                        user.getId(),
                                                        new Object[] { user.getId(), 0L, 0L, BigDecimal.ZERO });
                                        return UserResponse.builder()
                                                        .id(user.getId())
                                                        .fullName(user.getFullName())
                                                        .email(user.getEmail())
                                                        .phone(user.getPhone())
                                                        .provider(user.getAuthProvider().name())
                                                        .isActived(user.isActived())
                                                        .deletedAt(user.getDeletedAt())
                                                        .createdAt(user.getCreatedAt())
                                                        .totalOrders(((Long) stat[1]).intValue())
                                                        .completedOrders(((Long) stat[2]).intValue())
                                                        .totalSpending((BigDecimal) stat[3])
                                                        .deliveredOrders(ordersMap.getOrDefault(user.getId(),
                                                                        new ArrayList<>()))
                                                        .build();
                                })
                                .toList();

                // Sort theo spending nếu cần
                if ("spending_desc".equals(filter.getSortBy())) {
                        responses = responses.stream()
                                        .sorted(Comparator.comparing(UserResponse::getTotalSpending).reversed())
                                        .toList();
                }

                return new PageImpl<>(responses, pageable, users.getTotalElements());
        }

        @Override
        public void softDelete(Integer userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
                user.setDeletedAt(LocalDateTime.now());
                user.setActived(false);
                userRepository.save(user);
        }

        @Override
        public void restore(Integer userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
                user.setDeletedAt(null);
                userRepository.save(user);
        }

}
