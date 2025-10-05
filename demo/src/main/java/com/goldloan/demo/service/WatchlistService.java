package com.goldloan.demo.service;


import com.goldloan.demo.entity.Product;
import com.goldloan.demo.entity.User;
import com.goldloan.demo.entity.Watchlist;
import com.goldloan.demo.repository.ProductRepository;
import com.goldloan.demo.repository.UserRepository;
import com.goldloan.demo.repository.WatchlistRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    public void addToWatchlist(Long userId, Long productId) {
        if (watchlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("Product already in watchlist");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        
        Watchlist watchlistItem = Watchlist.builder()
            .user(user)
            .product(product)
            .build();
        
        watchlistRepository.save(watchlistItem);
    }
    
    public void removeFromWatchlist(Long userId, Long productId) {
        watchlistRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    public List<Watchlist> getUserWatchlist(Long userId) {
        return watchlistRepository.findByUserIdOrderByAddedAtDesc(userId);
    }
    
    public boolean isInWatchlist(Long userId, Long productId) {
        return watchlistRepository.existsByUserIdAndProductId(userId, productId);
    }
}
