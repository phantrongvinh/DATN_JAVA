package com.datn.project.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.datn.project.entity.Wishlist;
import com.datn.project.entity.WishlistId;

public interface IWishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    List<Wishlist> findById_UserId(Integer userId);

   boolean existsById_UserIdAndId_ProductId(Integer userId, Integer productId);

    void deleteById_UserIdAndId_ProductId(Integer userId, Integer productId);

    @Query("SELECT w.id.productId FROM Wishlist w WHERE w.id.userId = :userId")
    List<Integer> findProductIdsByUserId(Integer userId);

    @Query("""
        SELECT w.id.productId, COUNT(w)
        FROM Wishlist w
        WHERE w.createdAt >= :start AND w.createdAt < :end
        GROUP BY w.id.productId
    """)
    List<Object[]> countWishlistByProductInRange(LocalDateTime start, LocalDateTime end);
}
