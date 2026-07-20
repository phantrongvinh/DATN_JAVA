package com.datn.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datn.project.dto.voucher.BirthdayPreviewResponse;
import com.datn.project.dto.voucher.UserBirthdayDTO;
import com.datn.project.dto.voucher.VoucherRequest;
import com.datn.project.dto.voucher.VoucherResponse;
import com.datn.project.entity.DiscountType;
import com.datn.project.entity.User;
import com.datn.project.entity.Voucher;
import com.datn.project.repository.IUserRepository;
import com.datn.project.repository.IVoucherRepository;
import com.datn.project.specification.VoucherSpecification;

import jakarta.transaction.Transactional;

@Service
public class VoucherService implements IVoucherService {

    @Autowired
    private IVoucherRepository voucherRepository;

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private MailService mailService;

    private static final String[] MONTH_LABELS = {
            "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
            "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
    };

    @Override
    public Voucher validateVoucher(String code, BigDecimal orderTotal) throws BadRequestException {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Mã giảm giá không tồn tại"));

        LocalDateTime now = LocalDateTime.now();

        if (!voucher.isActive())
            throw new BadRequestException("Mã giảm giá không còn hiệu lực");
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate()))
            throw new BadRequestException("Mã giảm giá chưa đến thời gian sử dụng");
        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate()))
            throw new BadRequestException("Mã giảm giá đã hết hạn");
        if (voucher.getQuantity() != null && voucher.getUsedCount() >= voucher.getQuantity())
            throw new BadRequestException("Mã giảm giá đã hết lượt sử dụng");

        // Chỉ check minOrderValue khi orderTotal > 0
        if (orderTotal.compareTo(BigDecimal.ZERO) > 0
                && voucher.getMinOrderValue() != null
                && orderTotal.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new BadRequestException(
                    "Đơn hàng tối thiểu "
                            + NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                                    .format(voucher.getMinOrderValue())
                            + " để dùng mã này");
        }
        return voucher;
    }

    @Override
    public BigDecimal calcDiscount(BigDecimal orderTotal, Voucher voucher) {
        BigDecimal discount;
        if (voucher.getDiscountType() == DiscountType.PERCENT) {
            discount = orderTotal
                    .multiply(voucher.getDiscountValue().divide(BigDecimal.valueOf(100)))
                    .setScale(0, RoundingMode.HALF_UP);
            // giới hạn max discount nếu có
            if (voucher.getMaxDiscount() != null) {
                discount = discount.min(voucher.getMaxDiscount());
            }
        } else {
            discount = voucher.getDiscountValue().min(orderTotal);
        }
        return discount;

    }

    @Override
    @Transactional
    public void incrementUsedCount(Voucher voucher) {
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);
    }

    @Override
    public void decrementUsedCount(Voucher voucher) {
        if (voucher.getUsedCount() > 0) {
            voucher.setUsedCount(voucher.getUsedCount() - 1);
            voucherRepository.save(voucher);
        }
    }

    // phần fetch thông tin, tạo voucher, tự động gửi voucher cá nhân

    @Override
    public Page<VoucherResponse> fetchAll(int page, int size, String search,
            DiscountType discountType, Boolean isActive, Boolean isStackable, Boolean isPersonal) {
        Specification<Voucher> spec = VoucherSpecification.build(
                search, discountType, isActive, isStackable, isPersonal);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        return voucherRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    public VoucherResponse create(VoucherRequest req) {
        Voucher v = Voucher.builder()
                .code(req.getCode().toUpperCase())
                .description(req.getDescription())
                .discountType(req.getDiscountType())
                .discountValue(req.getDiscountValue())
                .minOrderValue(req.getMinOrderValue())
                .maxDiscount(req.getMaxDiscount())
                .quantity(req.getQuantity())
                .usedCount(0)
                .isStackable(req.isStackable())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .isActive(req.isActive())
                .build();
        return toResponse(voucherRepository.save(v));
    }

    @Override
    public void delete(Integer id) {
        voucherRepository.deleteById(id);

    }

    @Override
    public BirthdayPreviewResponse previewBirthdayVouchers() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        String monthKey = now.getYear() + String.format("%02d", month);

        List<User> birthdayUsers = userRepository.findBirthdayUsersByMonth(month);

        List<UserBirthdayDTO> willCreate = new ArrayList<>();
        List<UserBirthdayDTO> alreadyCreated = new ArrayList<>();

        for (User u : birthdayUsers) {
            String code = buildBirthdayCode(u.getId(), monthKey);
            UserBirthdayDTO dto = UserBirthdayDTO.builder()
                    .id(u.getId())
                    .fullName(u.getFullName())
                    .email(u.getEmail())
                    .birthDay(u.getBirthDay().format(DateTimeFormatter.ofPattern("dd/MM")))
                    .build();

            if (voucherRepository.existsByCode(code)) {
                alreadyCreated.add(dto);
            } else {
                willCreate.add(dto);
            }
        }

        return BirthdayPreviewResponse.builder()
                .monthLabel(MONTH_LABELS[month - 1])
                .willCreate(willCreate)
                .alreadyCreated(alreadyCreated)
                .build();
    }

    @Override
    public int generateBirthdayVouchers() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        String monthKey = now.getYear() + String.format("%02d", month);
        String monthLabel = MONTH_LABELS[month - 1];

        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        List<User> birthdayUsers = userRepository.findBirthdayUsersByMonth(month);
        int created = 0;

        for (User u : birthdayUsers) {
            String code = buildBirthdayCode(u.getId(), monthKey);
            if (voucherRepository.existsByCode(code))
                continue; // dedup

            Voucher v = Voucher.builder()
                    .code(code)
                    .description("Quà sinh nhật " + monthLabel + " dành riêng cho bạn. [" + monthKey + "]")
                    .discountType(DiscountType.PERCENT)
                    .discountValue(BigDecimal.valueOf(20))
                    .minOrderValue(BigDecimal.ZERO)
                    .quantity(1)
                    .usedCount(0)
                    .isStackable(false)
                    .startDate(LocalDateTime.now())
                    .endDate(endOfMonth)
                    .isActive(true)
                    .user(u)
                    .build();

            voucherRepository.save(v);
            mailService.sendBirthdayVoucherEmail(u, v, monthLabel);
            created++;
        }

        return created;
    }

    @Override
    public VoucherResponse update(Integer id, VoucherRequest req) {
        Voucher v = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        v.setCode(req.getCode().toUpperCase());
        v.setDescription(req.getDescription());
        v.setDiscountType(req.getDiscountType());
        v.setDiscountValue(req.getDiscountValue());
        v.setMinOrderValue(req.getMinOrderValue());
        v.setMaxDiscount(req.getMaxDiscount());
        v.setQuantity(req.getQuantity());
        v.setStackable(req.isStackable());
        v.setStartDate(req.getStartDate());
        v.setEndDate(req.getEndDate());
        v.setActive(req.isActive());

        return toResponse(voucherRepository.save(v));
    }

    private String buildBirthdayCode(Integer userId, String monthKey) {
        return "BDAY-" + userId + "-" + monthKey;
    }

    private VoucherResponse toResponse(Voucher v) {
        return VoucherResponse.builder()
                .id(v.getId())
                .code(v.getCode())
                .description(v.getDescription())
                .discountType(v.getDiscountType().name())
                .discountValue(v.getDiscountValue())
                .minOrderValue(v.getMinOrderValue())
                .maxDiscount(v.getMaxDiscount())
                .quantity(v.getQuantity())
                .usedCount(v.getUsedCount())
                .isStackable(v.isStackable())
                .startDate(v.getStartDate())
                .endDate(v.getEndDate())
                .isActive(v.isActive())
                .createdAt(v.getCreatedAt())
                .userId(v.getUser() != null ? v.getUser().getId() : null)
                .userEmail(v.getUser() != null ? v.getUser().getEmail() : null)
                .build();
    }
}
