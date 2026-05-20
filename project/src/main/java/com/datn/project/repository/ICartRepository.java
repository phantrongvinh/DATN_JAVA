package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Cart;

public interface ICartRepository extends JpaRepository<Cart, Integer> {

}
