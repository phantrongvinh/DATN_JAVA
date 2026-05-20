package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Address;

public interface IAddressRepository extends JpaRepository<Address, Integer> {

}
