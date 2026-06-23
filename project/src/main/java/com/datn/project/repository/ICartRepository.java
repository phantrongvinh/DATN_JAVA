package com.datn.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Cart;

public interface ICartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUserId(int userId);
}
