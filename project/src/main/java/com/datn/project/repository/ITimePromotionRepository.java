package com.datn.project.repository;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datn.project.entity.TimePromotion;

public interface ITimePromotionRepository extends JpaRepository<TimePromotion, Integer> {

    @Query("""
                SELECT t FROM TimePromotion t
                WHERE t.isActive = true
                AND :now BETWEEN t.startTime AND t.endTime
            """)
    Optional<TimePromotion> findActiveByTime(@Param("now") LocalTime now);
}
