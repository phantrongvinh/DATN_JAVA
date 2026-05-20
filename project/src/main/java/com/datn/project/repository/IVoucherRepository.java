package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Voucher;

public interface IVoucherRepository extends JpaRepository<Voucher, Integer> {

}
