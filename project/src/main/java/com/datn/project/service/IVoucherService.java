package com.datn.project.service;

import java.math.BigDecimal;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

import com.datn.project.dto.voucher.BirthdayPreviewResponse;
import com.datn.project.dto.voucher.VoucherRequest;
import com.datn.project.dto.voucher.VoucherResponse;
import com.datn.project.entity.DiscountType;
import com.datn.project.entity.Voucher;

public interface IVoucherService {
    Voucher validateVoucher(String code, BigDecimal orderTotal) throws BadRequestException;

    BigDecimal calcDiscount(BigDecimal orderTotal, Voucher voucher);

    void incrementUsedCount(Voucher voucher);

    void decrementUsedCount(Voucher voucher);

    // fetch
    Page<VoucherResponse> fetchAll(int page, int size, String search,
            DiscountType discountType, Boolean isActive, Boolean isStackable, Boolean isPersonal);

    VoucherResponse create(VoucherRequest req);

    VoucherResponse update(Integer id, VoucherRequest req);

    void delete(Integer id);

    BirthdayPreviewResponse previewBirthdayVouchers();
    
    int generateBirthdayVouchers();
}
