package com.goldloan.demo.repository;

import com.goldloan.demo.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    // Get all watchlist items for a user (sorted by newest first)
    List<Watchlist> findByUserIdOrderByAddedAtDesc(Long userId);

    // Check if a specific product is already in the user's watchlist
    Optional<Watchlist> findByUserIdAndProductId(Long userId, Long productId);

    // Remove a product from a user's watchlist
    void deleteByUserIdAndProductId(Long userId, Long productId);

    // Verify if a product exists in a user's watchlist
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
