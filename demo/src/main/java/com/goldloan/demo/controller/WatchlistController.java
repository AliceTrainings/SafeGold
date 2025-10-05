package com.goldloan.demo.controller;

import com.goldloan.demo.dto.WatchlistItemDto;
import com.goldloan.demo.entity.ProductImage;
import com.goldloan.demo.entity.User;
import com.goldloan.demo.entity.Watchlist;
import com.goldloan.demo.service.WatchlistService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping("/{productId}")
    public ResponseEntity<String> addToWatchlist(@PathVariable Long productId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        try {
            watchlistService.addToWatchlist(user.getId(), productId);
            return ResponseEntity.ok("Product added to watchlist");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromWatchlist(@PathVariable Long productId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        watchlistService.removeFromWatchlist(user.getId(), productId);
        return ResponseEntity.ok("Product removed from watchlist");
    }

    @GetMapping
    public ResponseEntity<List<WatchlistItemDto>> getUserWatchlist(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Watchlist> watchlist = watchlistService.getUserWatchlist(user.getId());

        List<WatchlistItemDto> response = watchlist.stream()
            .map(item -> WatchlistItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productPrice(item.getProduct().getPrice())
                .productImageUrl(item.getProduct().getImages().stream()
                    .filter(ProductImage::getIsPrimary)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(null))
                .addedAt(item.getAddedAt())
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
