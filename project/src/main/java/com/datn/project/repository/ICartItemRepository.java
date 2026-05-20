package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.CartItem;

public interface ICartItemRepository extends JpaRepository<CartItem, Integer> {

}
