package com.datn.project.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.datn.project.dto.PromotionRequest;
import com.datn.project.dto.TimePromotionRequest;
import com.datn.project.dto.dashboard.DashboardResponse;
import com.datn.project.dto.order.OrderFilterDTO;
import com.datn.project.dto.order.ReturnResponse;
import com.datn.project.dto.product.AddPromotionToProductsRequest;
import com.datn.project.dto.product.AdminProductFilterDTO;
import com.datn.project.dto.product.ProductRequest;
import com.datn.project.dto.user.UserFilterDTO;
import com.datn.project.dto.voucher.BirthdayPreviewResponse;
import com.datn.project.dto.voucher.VoucherRequest;
import com.datn.project.dto.voucher.VoucherResponse;
import com.datn.project.entity.DiscountType;
import com.datn.project.entity.OrderStatus;
import com.datn.project.entity.PaymentStatus;
import com.datn.project.entity.ReturnRequest;
import com.datn.project.entity.ReturnStatus;
import com.datn.project.entity.SiteSetting;
import com.datn.project.repository.IReturnRequestRepository;
import com.datn.project.repository.ISiteSettingRepository;
import com.datn.project.repository.ITimePromotionRepository;
import com.datn.project.service.IDashboardService;
import com.datn.project.service.IOrderService;
import com.datn.project.service.IProductReviewService;
import com.datn.project.service.IProductService;
import com.datn.project.service.IPromotionService;
import com.datn.project.service.IReturnRequestService;
import com.datn.project.service.ITimePromotionService;
import com.datn.project.service.IUserService;
import com.datn.project.service.IVoucherService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/api/v1/admin")
public class AdminController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IPromotionService promotionService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ITimePromotionService timePromotionService;

    @Autowired
    private ITimePromotionRepository timePromotionRepository;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IDashboardService dashboardService;

    @Autowired
    private ISiteSettingRepository siteSettingRepository;

    @Autowired
    private IVoucherService voucherService;

    @Autowired
    private IProductReviewService reviewService;

    @Autowired
    private IReturnRequestService returnService;
    @Autowired
    private IReturnRequestRepository returnRequestRepository;

    // phần products
    @GetMapping("/products/top5")
    public ResponseEntity<?> getTop5Product() {
        return ResponseEntity.ok(productService.getTop5Product()).getBody();
    }

    @GetMapping("/products/all")
    public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) Integer audienceId,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) Integer categoryId, @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean onSale,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy) {
        AdminProductFilterDTO filter = new AdminProductFilterDTO();
        filter.setAudienceId(audienceId);
        filter.setBrandId(brandId);
        filter.setCategoryId(categoryId);
        filter.setSearch(search);
        filter.setOnSale(onSale);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setSortBy(sortBy);

        return ResponseEntity.ok(productService.getAllProducts(page, size, filter)).getBody();
    }

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam("data") String data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            ProductRequest request = objectMapper.readValue(data, ProductRequest.class);
            return productService.createProduct(request, images);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
        }
    }

    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestParam("data") String data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            ProductRequest request = objectMapper.readValue(data, ProductRequest.class);
            return productService.updateProduct(id, request, images);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deactivateProduct(@PathVariable int id) {
        return ResponseEntity.ok(productService.deactivateProduct(id)).getBody();
    }

    // phần khách hàng
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String birthDayFrom,
            @RequestParam(required = false) String birthDayTo,
            @RequestParam(required = false) String createdAtFrom,
            @RequestParam(required = false) String createdAtTo) {
        UserFilterDTO filter = new UserFilterDTO();
        filter.setSearch(search);
        filter.setIsActive(isActive);
        filter.setIsDeleted(isDeleted);
        filter.setSortBy(sortBy);
        filter.setBirthDayFrom(
                (birthDayFrom != null && !birthDayFrom.isBlank()) ? LocalDate.parse(birthDayFrom) : null);
        filter.setBirthDayTo(
                (birthDayTo != null && !birthDayTo.isBlank()) ? LocalDate.parse(birthDayTo) : null);
        filter.setCreatedAtFrom(
                (createdAtFrom != null && !createdAtFrom.isBlank()) ? LocalDateTime.parse(createdAtFrom) : null);
        filter.setCreatedAtTo(
                (createdAtTo != null && !createdAtTo.isBlank()) ? LocalDateTime.parse(createdAtTo) : null);

        return ResponseEntity.ok(userService.getAllUsers(filter, page, size));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> softDelete(@PathVariable Integer id) {
        userService.softDelete(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa khách hàng"));
    }

    @PatchMapping("/users/{id}/restore")
    public ResponseEntity<?> restore(@PathVariable Integer id) {
        userService.restore(id);
        return ResponseEntity.ok(Map.of("message", "Đã khôi phục khách hàng"));
    }

    // @GetMapping("/users/{id}")
    // public ResponseEntity<?> getUserById(@PathVariable(value = "id") int id) {
    // return ResponseEntity.ok(userService.getUserById(id)).getBody();
    // }

    // phần khuyến mãi
    @PostMapping("/promotions")
    public ResponseEntity<?> createPromotion(@RequestBody PromotionRequest req) {
        return ResponseEntity.ok(promotionService.createPromotion(req));
    }

    @GetMapping("/promotions")
    public ResponseEntity<?> getAllActivePromotion() {
        return ResponseEntity.ok(promotionService.getAllActivePromotioEntity()).getBody();
    }

    @PostMapping("/promotions/assign")
    public ResponseEntity<?> addPromotionToProducts(
            @RequestBody AddPromotionToProductsRequest request) {
        productService.addPromotionToProducts(request);
        return ResponseEntity.ok("Áp dụng khuyến mãi thành công");
    }

    @GetMapping("/promotions/all")
    public ResponseEntity<?> getAllPromotion(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search,
            @RequestParam(required = false) DiscountType discountType,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(promotionService.getAllPromotion(page, size, search, discountType, status)).getBody();
    }

    // time promotion
    @GetMapping("/time-promotions/all")
    public ResponseEntity<?> getAllTimePromotion(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(timePromotionService.getAllTimePromotion(page, size, search, isActive)).getBody();
    }

    @PutMapping("/time-promotions/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody TimePromotionRequest request) {
        return ResponseEntity.ok(timePromotionService.updateTimePromotion(id, request)).getBody();
    }

    @PostMapping("/time-promotions")
    public ResponseEntity<?> createTimePromotion(@RequestBody TimePromotionRequest request) {
        return ResponseEntity.ok(timePromotionService.createTimePromotion(request)).getBody();
    }

    @PatchMapping("/time-promotions/{id}")
    public ResponseEntity<?> toggle(@PathVariable int id) {
        return ResponseEntity.ok(timePromotionService.toggleActive(id)).getBody();
    }

    @DeleteMapping("/time-promotions/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        timePromotionRepository.deleteById(id);
        return ResponseEntity.ok("Xóa thành công");
    }

    // phần order
    // @PatchMapping("/orders/{orderId}/status")
    // public ResponseEntity<?> updateStatus(
    // @PathVariable Integer orderId,
    // @RequestParam String status) {
    // OrderStatus orderStatus = status != null ?
    // OrderStatus.valueOf(status.toUpperCase()) : null;
    // if (orderStatus == OrderStatus.DELIVERED) {
    // throw new RuntimeException("Trạng thái 'Đã giao' chỉ được cập nhật tự động từ
    // đơn vị vận chuyển");
    // }
    // orderService.updateOrderStatus(orderId, orderStatus);
    // return ResponseEntity.ok("Cập nhật trạng thái thành công");
    // }

    @PatchMapping("/orders/{id}/advance")
    public ResponseEntity<?> advanceOrder(@PathVariable Integer id) {
        orderService.advanceStatus(id);
        return ResponseEntity.ok(Map.of("message", "Cập nhật thành công"));
    }

    @PatchMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(Map.of("message", "Đã hủy đơn hàng"));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) Integer paymentMethodId,
            @RequestParam(required = false) LocalDateTime dateFrom,
            @RequestParam(required = false) LocalDateTime dateTo, @RequestParam(required = false) String sortBy) {

        PaymentStatus paymentStatusEnum = paymentStatus != null || paymentStatus == ""
                ? PaymentStatus.valueOf(paymentStatus.toUpperCase())
                : null;

        OrderFilterDTO filterDTO = new OrderFilterDTO(search, status,
                paymentStatusEnum, paymentMethodId, dateFrom, dateTo, sortBy);

        return ResponseEntity.ok(orderService.getAllOrders(page, size, filterDTO)).getBody();
    }

    @PatchMapping("/orders/{orderId}/confirm")
    public ResponseEntity<?> confirmOrder(
            @PathVariable Integer orderId) {

        orderService.confirmOrder(orderId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Xác nhận đơn hàng thành công"));
    }

    // return request
    @GetMapping("/returns")
    public ResponseEntity<?> getAllReturns(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ReturnStatus status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<ReturnRequest> result = status != null
                ? returnRequestRepository.findByStatus(status, pageable)
                : returnRequestRepository.findAll(pageable);
        return ResponseEntity.ok(result.map(this::toResponse));
    }

    @PatchMapping("/returns/{id}")
    public ResponseEntity<?> resolveReturn(
            @PathVariable Integer id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String adminNote) {
        returnService.resolveReturn(id, approved, adminNote);
        return ResponseEntity.ok(Map.of("message", "Đã xử lý yêu cầu trả hàng"));
    }

    @PatchMapping("/returns/{id}/complete")
    public ResponseEntity<?> manualComplete(@PathVariable Integer id) {
        returnService.manualCompleteReturn(id);
        return ResponseEntity.ok(Map.of("message", "Đã xác nhận hoàn tất trả hàng"));
    }

    private ReturnResponse toResponse(ReturnRequest rr) {
        return ReturnResponse.builder()
                .id(rr.getId())
                .orderId(rr.getOrder().getId())
                .receiverName("Maison Calcio")
                .fromPhone(rr.getOrder().getReceiverPhone())
                .toPhone("0909090909")
                .address(rr.getOrder().getShippingAddress())
                .reason(rr.getReason())
                .images(rr.getImages() != null && !rr.getImages().isBlank()
                        ? List.of(rr.getImages().split(","))
                        : List.of())
                .status(rr.getStatus().name())
                .adminNote(rr.getAdminNote())
                .createdAt(rr.getCreatedAt())
                .build();
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<?> getOrderDetail(@PathVariable Integer id) {
    // return ResponseEntity.ok(orderService.getOrderDetail(id));
    // }

    // Dashboard
    @GetMapping("/dashboard")
    public DashboardResponse getDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Integer chartYear) {
        return dashboardService.getDashboard(start, end, chartYear);
    }

    // site setting
    // Admin - load và save
    @GetMapping("/site-setting")
    public ResponseEntity<?> get() throws Exception {
        SiteSetting setting = siteSettingRepository.findById(1)
                .orElseThrow();
        return ResponseEntity.ok(objectMapper.readValue(setting.getData(), Map.class));
    }

    @PutMapping("/site-setting")
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body) throws Exception {
        SiteSetting setting = siteSettingRepository.findById(1)
                .orElse(new SiteSetting());
        setting.setData(objectMapper.writeValueAsString(body));
        setting.setUpdatedAt(LocalDateTime.now());
        siteSettingRepository.save(setting);
        return ResponseEntity.ok(body);
    }

    // phần voucher
    @GetMapping("/vouchers")
    public Page<VoucherResponse> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) DiscountType discountType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isStackable,
            @RequestParam(required = false) Boolean isPersonal) {
        return voucherService.fetchAll(page, size, search, discountType, isActive, isStackable, isPersonal);
    }

    @PostMapping("/vouchers")
    public VoucherResponse create(@RequestBody VoucherRequest req) {
        return voucherService.create(req);
    }

    @PutMapping("/vouchers/{id}")
    public VoucherResponse update(@PathVariable Integer id, @RequestBody VoucherRequest req) {
        return voucherService.update(id, req);
    }

    @DeleteMapping("/vouchers/{id}")
    public void delete(@PathVariable Integer id) {
        voucherService.delete(id);
    }

    @GetMapping("/vouchers/birthday/preview")
    public BirthdayPreviewResponse previewBirthday() {
        return voucherService.previewBirthdayVouchers();
    }

    @PostMapping("/vouchers/birthday/generate")
    public Map<String, Object> generateBirthday() {
        int created = voucherService.generateBirthdayVouchers();
        return Map.of("created", created);
    }

    // review
    @DeleteMapping("/reviews/{userId}/{productId}")
    public ResponseEntity<?> hideReview(@PathVariable Integer userId, @PathVariable Integer productId) {
        reviewService.hideReview(userId, productId);
        return ResponseEntity.ok(Map.of("message", "Đã ẩn đánh giá"));
    }

}
