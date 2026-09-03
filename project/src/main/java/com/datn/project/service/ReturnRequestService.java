package com.datn.project.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.datn.project.entity.Order;
import com.datn.project.entity.OrderDetail;
import com.datn.project.entity.OrderStatus;
import com.datn.project.entity.PaymentStatus;
import com.datn.project.entity.ReturnRequest;
import com.datn.project.entity.ReturnStatus;
import com.datn.project.entity.Voucher;
import com.datn.project.repository.IOrderDetailRepository;
import com.datn.project.repository.IOrderRepository;
import com.datn.project.repository.IReturnRequestRepository;
import com.datn.project.repository.IVoucherRepository;

import jakarta.transaction.Transactional;

@Service
public class ReturnRequestService implements IReturnRequestService {

    @Autowired
    private IReturnRequestRepository returnRequestRepository;
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private GHNService ghnService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private IOrderDetailRepository orderDetailRepository;
    @Autowired
    private LoyaltyPointService loyaltyPointService;
    @Autowired
    private IVoucherRepository voucherRepository;
    @Autowired
    private IVoucherService voucherService;

    private static final int RETURN_WINDOW_DAYS = 7;

    @Transactional
    @Override
    public void requestReturn(Integer userId, Integer orderId, String reason, List<MultipartFile> images) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (!Integer.valueOf(order.getUser().getId()).equals(userId)) {
            throw new RuntimeException("Không có quyền với đơn hàng này");
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Chỉ có thể yêu cầu trả hàng cho đơn đã giao");
        }
        if (order.getDeliveredAt() == null
                || order.getDeliveredAt().plusDays(RETURN_WINDOW_DAYS).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Đã hết hạn " + RETURN_WINDOW_DAYS + " ngày yêu cầu trả hàng");
        }
        if (returnRequestRepository.findByOrderIdAndStatus(orderId, ReturnStatus.PENDING).isPresent()) {
            throw new RuntimeException("Đơn hàng đã có yêu cầu trả hàng đang chờ xử lý");
        }

        List<String> imageUrls = images != null
                ? images.stream().map(cloudinaryService::uploadImage).toList()
                : List.of();

        ReturnRequest rr = ReturnRequest.builder()
                .order(order)
                .reason(reason)
                .images(String.join(",", imageUrls))
                .status(ReturnStatus.PENDING)
                .build();
        returnRequestRepository.save(rr);

        order.setStatus(OrderStatus.RETURN_REQUESTED);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void resolveReturn(Integer returnRequestId, boolean approved, String adminNote) {
        ReturnRequest rr = returnRequestRepository.findById(returnRequestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu trả hàng không tồn tại"));
        Order order = rr.getOrder();

        rr.setAdminNote(adminNote);
        rr.setResolvedAt(LocalDateTime.now());

        if (approved) {
            rr.setStatus(ReturnStatus.APPROVED);
            String ghnCode = ghnService.createReturnOrder(order);
            rr.setGhnReturnCode(ghnCode);
        } else {
            rr.setStatus(ReturnStatus.REJECTED);
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
        }

        returnRequestRepository.save(rr);
    }

    @Transactional
    @Override
    public void completeReturn(Integer returnRequestId) {
        ReturnRequest rr = returnRequestRepository.findById(returnRequestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu trả hàng không tồn tại"));

        rr.setStatus(ReturnStatus.COMPLETED);
        returnRequestRepository.save(rr);

        Order order = rr.getOrder();
        order.setStatus(OrderStatus.RETURNED);
        order.setPaymentStatus(PaymentStatus.REFUNDED);
        if(order.getVoucher() != null){
            Voucher voucher = voucherRepository.findById(order.getVoucher().getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy voucher tương ứng"));
            voucherService.decrementUsedCount(voucher);
        }

        orderRepository.save(order);
        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        loyaltyPointService.deductForReturnedOrder(order.getUser(), details);
    }

    @Transactional
    @Override
    public void manualCompleteReturn(Integer returnRequestId) {
        completeReturn(returnRequestId);
    }

    @Override
    public void updateReturnTrackingStatus(Integer returnRequestId, ReturnStatus status) {
        ReturnRequest rr = returnRequestRepository.findById(returnRequestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu trả hàng không tồn tại"));

        if (status == ReturnStatus.COMPLETED) {
            completeReturn(returnRequestId);
        }

        rr.setStatus(status);
        returnRequestRepository.save(rr);

    }

}