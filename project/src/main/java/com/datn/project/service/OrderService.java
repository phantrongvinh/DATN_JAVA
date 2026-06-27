package com.datn.project.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.datn.project.dto.order.MyOrderResponse;
import com.datn.project.dto.order.OrderDetailResponse;
import com.datn.project.dto.order.OrderItemRequest;
import com.datn.project.dto.order.OrderRequest;
import com.datn.project.dto.order.OrderResponse;
import com.datn.project.entity.Order;
import com.datn.project.entity.OrderDetail;
import com.datn.project.entity.OrderStatus;
import com.datn.project.entity.PaymentMethod;
import com.datn.project.entity.PaymentStatus;
import com.datn.project.entity.ProductVariant;
import com.datn.project.entity.Promotion;
import com.datn.project.entity.TimePromotion;
import com.datn.project.entity.User;
import com.datn.project.entity.Voucher;
import com.datn.project.repository.IOrderRepository;
import com.datn.project.repository.IPaymentMethodRepository;
import com.datn.project.repository.IProductVariantRepository;
import com.datn.project.repository.IUserRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService implements IOrderService {

        @Autowired
        private IOrderRepository orderRepository;

        @Autowired
        private IProductVariantRepository productVariantRepository;

        @Autowired
        private IPaymentMethodRepository paymentMethodRepository;

        @Autowired
        private IPromotionService promotionService;

        @Autowired
        private ITimePromotionService timePromotionService;

        @Autowired
        private IVoucherService voucherService;

        @Autowired
        private IUserRepository userRepository;

        @Autowired
        private ICartService cartService;

        @Autowired
        private GHNService ghnService;

        @Transactional
        @Override
        public ResponseEntity<?> placeOrder(Integer userId, OrderRequest request) throws BadRequestException {
                Order order = new Order();

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

                order.setUser(user);
                order.setShippingAddress(request.getShippingAddress());
                order.setReceiverName(request.getReceiverName());
                order.setReceiverPhone(request.getReceiverPhone());

                PaymentMethod paymentMethod = paymentMethodRepository
                                .findById(request.getPaymentMethodId())
                                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không tồn tại"));
                order.setPaymentMethod(paymentMethod);

                // ─── 1. Xử lý từng item, tính subtotal ──────────────
                List<OrderDetail> details = new ArrayList<>();
                BigDecimal totalPrice = BigDecimal.ZERO;

                for (OrderItemRequest req : request.getItems()) {
                        ProductVariant variant = productVariantRepository
                                        .findByIdWithProduct(req.getVariantId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Variant không tồn tại: " + req.getVariantId()));

                        int updated = productVariantRepository.decreaseStock(variant.getId(), req.getQuantity());
                        if (updated == 0)
                                throw new RuntimeException(
                                                "Sản phẩm " + variant.getProduct().getName() + " không đủ tồn kho");

                        Optional<Promotion> productPromo = promotionService
                                        .getActivePromotion(variant.getProduct().getId());

                        BigDecimal unitPrice = productPromo
                                        .map(p -> promotionService.calcDiscountedPrice(variant.getPrice(), p))
                                        .orElse(variant.getPrice());

                        totalPrice = totalPrice.add(unitPrice.multiply(BigDecimal.valueOf(req.getQuantity())));

                        OrderDetail detail = new OrderDetail();
                        detail.setOrder(order);
                        detail.setProductVariant(variant);
                        detail.setQuantity(req.getQuantity());
                        detail.setProductName(variant.getProduct().getName());
                        detail.setColor(variant.getColor());
                        detail.setSizeName(variant.getSize().getName());
                        detail.setPrice(unitPrice);
                        detail.setPromotion(productPromo.orElse(null));
                        details.add(detail);
                }

                // biến effectively final để dùng trong lambda bên dưới
                final BigDecimal finalTotalPrice = totalPrice;

                // ─── 2. Apply voucher ────────────────────────────────
                BigDecimal discountAmount = BigDecimal.ZERO;
                if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
                        Voucher voucher = voucherService.validateVoucher(request.getVoucherCode(), finalTotalPrice);
                        discountAmount = voucherService.calcDiscount(finalTotalPrice, voucher);
                        voucherService.incrementUsedCount(voucher);
                        order.setVoucher(voucher);
                }

                // ─── 3. Apply time promotion ─────────────────────────
                final BigDecimal afterVoucher = finalTotalPrice.subtract(discountAmount);
                Optional<TimePromotion> timePromo = timePromotionService.getActiveTimePromotion();
                BigDecimal timeDiscount = timePromo
                                .map(p -> timePromotionService.calcDiscount(afterVoucher, p))
                                .orElse(BigDecimal.ZERO);

                // ─── 4. Final price ───────────────────────────────────
                BigDecimal finalPrice = finalTotalPrice
                                .subtract(discountAmount)
                                .subtract(timeDiscount)
                                .max(BigDecimal.ZERO);

                order.setOrderDetails(details);
                order.setTotalPrice(finalTotalPrice);
                order.setDiscountAmount(discountAmount);
                order.setTimeDiscount(timeDiscount);
                order.setFinalPrice(finalPrice);
                order.setTimePromotion(timePromo.orElse(null));
                Order savedOrder = orderRepository.save(order);

                // ─── COD: confirm luôn + tạo shipment ────────────
                if (request.getPaymentMethodId() == 1) {
                        savedOrder.setPaymentStatus(PaymentStatus.PAID);
                        savedOrder.setStatus(OrderStatus.CONFIRMED);
                        orderRepository.save(savedOrder);
                        ghnService.createShipment(savedOrder.getId());
                        cartService.clearCart(userId);
                }

                // ─── VNPAY: chỉ tạo order, chờ callback ─────────
                if (request.getPaymentMethodId() == 2) {
                        savedOrder.setPaymentStatus(PaymentStatus.PENDING);
                        savedOrder.setStatus(OrderStatus.PENDING);
                        orderRepository.save(savedOrder);
                        // KHÔNG gọi GHN và KHÔNG clear cart ở đây
                        // GHN + clear cart sẽ được gọi trong vnpayCallback sau khi thanh toán thành
                        // công
                }

                return ResponseEntity.ok(order.getId());
        }

        @Override
        public OrderResponse mapToResponse(Order order) {
                List<OrderDetailResponse> items = order.getOrderDetails().stream()
                                .map(d -> OrderDetailResponse.builder()
                                                .productVariantId(d.getProductVariant().getId())
                                                .productName(d.getProductName())
                                                .color(d.getColor())
                                                .sizeName(d.getSizeName())
                                                .quantity(d.getQuantity())
                                                .originalPrice(d.getProductVariant().getPrice())
                                                .price(d.getPrice())
                                                .promotionName(d.getPromotion() != null ? d.getPromotion().getName()
                                                                : null)
                                                .build())
                                .toList();

                return OrderResponse.builder()
                                .id(order.getId())
                                .items(items)
                                .totalPrice(order.getTotalPrice())
                                .discountAmount(order.getDiscountAmount())
                                .timeDiscount(order.getTimeDiscount())
                                .finalPrice(order.getFinalPrice())
                                .voucherCode(order.getVoucher() != null ? order.getVoucher().getCode() : null)
                                .timePromotionName(order.getTimePromotion() != null ? order.getTimePromotion().getName()
                                                : null)
                                .status(order.getStatus().name())
                                .shippingAddress(order.getShippingAddress())
                                .receiverName(order.getReceiverName())
                                .receiverPhone(order.getReceiverPhone())
                                .createdAt(order.getCreatedAt())
                                .build();
        }

        @Override
        public Order findById(Integer orderId) {
                return orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Đơn hàng không tồn tại không tồn tại: " + orderId));
        }

        @Transactional
        public void confirmPayment(Integer orderId, String transactionId) {
                Order order = findById(orderId);
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setPaymentTransactionId(transactionId);
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
        }

        @Override
        public ResponseEntity<?> getOrdersByIdByUser(int id, int userid) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

                if (order.getUser().getId() != userid) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body("Bạn không có quyền xem đơn hàng này");
                }

                OrderResponse response = new OrderResponse();

                response.setItems(order.getOrderDetails().stream().map(i -> {
                        OrderDetailResponse item = new OrderDetailResponse(i.getId(), i.getProductName(), i.getColor(),
                                        i.getSizeName(), i.getQuantity(), null, i.getPrice(),
                                        i.getPromotion() != null ? i.getPromotion().getName() : null);

                        return item;

                }).toList());
                response.setCreatedAt(order.getCreatedAt());
                response.setDiscountAmount(order.getDiscountAmount());
                response.setFinalPrice(order.getFinalPrice());
                response.setId(order.getId());
                response.setPaymentStatus(order.getPaymentStatus().name());
                response.setReceiverName(order.getReceiverName());
                response.setReceiverPhone(order.getReceiverPhone());
                response.setShippingAddress(order.getShippingAddress());
                response.setStatus(order.getStatus().name());
                response.setTimeDiscount(order.getTimeDiscount());
                response.setTimePromotionName(
                                order.getTimePromotion() != null ? order.getTimePromotion().getName() : null);
                response.setTotalPrice(order.getTotalPrice());
                response.setTrackingCode(order.getTrackingCode());
                response.setVoucherCode(order.getVoucher() != null ? order.getVoucher().getCode() : null);
                response.setPaymentMethod(order.getPaymentMethod().getName());

                return ResponseEntity.ok(response);
        }

        @Override
        public ResponseEntity<?> getAllMyOrder(int userId) {
                List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

                if (orders.isEmpty()) {
                        return ResponseEntity.ok(Map.of("message", "Chưa có đơn hàng"));
                }

                List<MyOrderResponse> responses = orders.stream().map(o -> {
                        MyOrderResponse response = new MyOrderResponse();
                        response.setId(o.getId());
                        response.setCreatedAt(o.getCreatedAt());
                        response.setItems(o.getOrderDetails().stream().map(i -> {
                                OrderDetailResponse orderDetailResponse = new OrderDetailResponse(i.getId(),
                                                i.getProductName(), i.getColor(), i.getSizeName(), i.getQuantity(),
                                                null, i.getPrice(),
                                                i.getPromotion() != null ? i.getPromotion().getName() : null);
                                return orderDetailResponse;
                        }).toList());

                        response.setPaymentStatus(null);
                        response.setPrice(o.getFinalPrice());
                        response.setStatus(o.getStatus().name());
                        response.setTrackingCode(o.getTrackingCode());
                        response.setPaymentMethod(o.getPaymentMethod().getName());
                        return response;
                }).toList();
                return ResponseEntity.ok(responses);
        }
}
