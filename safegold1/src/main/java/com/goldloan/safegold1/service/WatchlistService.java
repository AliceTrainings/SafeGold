package com.goldloan.safegold1.service;

import com.goldloan.safegold1.model.Product;
import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.ProductRepository;
import com.goldloan.safegold1.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.HashSet;

@Service
@Transactional
public class WatchlistService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WatchlistService(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Add a product to user's watchlist
     * @param userId User ID
     * @param productId Product ID
     * @return true if added successfully, false if already exists
     */
    public boolean addToWatchlist(Long userId, Long productId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            Product product = productRepository.findById(productId).orElse(null);
            
            if (user == null) {
                System.err.println("User not found with ID: " + userId);
                return false;
            }
            
            if (product == null) {
                System.err.println("Product not found with ID: " + productId);
                return false;
            }
            
            Set<Product> watchlist = user.getWatchlist();
            if (watchlist == null) {
                watchlist = new HashSet<>();
                user.setWatchlist(watchlist);
            }
            
            boolean added = watchlist.add(product);
            
            if (added) {
                userRepository.save(user);
                System.out.println("Product " + productId + " added to watchlist for user " + userId);
            } else {
                System.out.println("Product " + productId + " already in watchlist for user " + userId);
            }
            
            return added;
        } catch (Exception e) {
            System.err.println("Error adding product to watchlist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a product from user's watchlist
     * @param userId User ID
     * @param productId Product ID
     * @return true if removed successfully, false if not found
     */
    public boolean removeFromWatchlist(Long userId, Long productId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            Product product = productRepository.findById(productId).orElse(null);
            
            if (user == null) {
                System.err.println("User not found with ID: " + userId);
                return false;
            }
            
            if (product == null) {
                System.err.println("Product not found with ID: " + productId);
                return false;
            }
            
            Set<Product> watchlist = user.getWatchlist();
            if (watchlist == null || watchlist.isEmpty()) {
                System.out.println("User " + userId + " has no items in watchlist");
                return false;
            }
            
            boolean removed = watchlist.remove(product);
            
            if (removed) {
                userRepository.save(user);
                System.out.println("Product " + productId + " removed from watchlist for user " + userId);
            } else {
                System.out.println("Product " + productId + " not found in watchlist for user " + userId);
            }
            
            return removed;
        } catch (Exception e) {
            System.err.println("Error removing product from watchlist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if a product is in user's watchlist
     * @param userId User ID
     * @param productId Product ID
     * @return true if product is in watchlist, false otherwise
     */
    public boolean isInWatchlist(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        
        if (user == null || product == null) {
            return false;
        }
        
        return user.getWatchlist().contains(product);
    }

    /**
     * Get user's watchlist
     * @param userId User ID
     * @return Set of products in watchlist
     */
    public Set<Product> getUserWatchlist(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getWatchlist() : null;
    }

    /**
     * Get watchlist count for a user
     * @param userId User ID
     * @return Number of items in watchlist
     */
    public int getWatchlistCount(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getWatchlist().size() : 0;
    }
}