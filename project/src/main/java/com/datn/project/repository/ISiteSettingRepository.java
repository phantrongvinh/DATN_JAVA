package com.datn.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.project.entity.SiteSetting;

public interface ISiteSettingRepository extends JpaRepository<SiteSetting,Integer> {
    
}
