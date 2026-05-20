package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.Gender;

public interface IGenderRepository extends JpaRepository<Gender, Integer> {

}
