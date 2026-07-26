package com.datn.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.datn.project.entity.Voucher;

public interface IVoucherRepository extends JpaRepository<Voucher, Integer>, JpaSpecificationExecutor<Voucher> {
    Optional<Voucher> findByCode(String code);

    boolean existsByCode(String code);

    List<Voucher> findByUserIsNullOrderByStartDateDesc();
}
