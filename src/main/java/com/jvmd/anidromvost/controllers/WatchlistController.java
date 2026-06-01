package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.User;
import com.jvmd.anidromvost.model.WatchlistEntry;
import com.jvmd.anidromvost.service.UserService;
import com.jvmd.anidromvost.service.WatchlistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/watchlist")
@AllArgsConstructor
public class WatchlistController {
    private WatchlistService watchlistService;
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<WatchlistEntry>> getWatchlist(@AuthenticationPrincipal UserDetails principal) {
        User user = userService.findByUsername(principal.getUsername());
        return ResponseEntity.ok(watchlistService.getWatchlist(user.getId()));
    }

    @PostMapping("/{animeId}")
    public ResponseEntity<WatchlistEntry> add(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long animeId) {
        User user = userService.findByUsername(principal.getUsername());
        return ResponseEntity.ok(watchlistService.addToWatchlist(user, animeId));
    }

    @DeleteMapping("/{animeId}")
    public ResponseEntity<Void> remove(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long animeId) {
        User user = userService.findByUsername(principal.getUsername());
        watchlistService.removeFromWatchlist(user.getId(), animeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{animeId}/favorite")
    public ResponseEntity<WatchlistEntry> toggleFavorite(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long animeId) {
        User user = userService.findByUsername(principal.getUsername());
        return ResponseEntity.ok(watchlistService.toggleFavorite(user.getId(), animeId));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<WatchlistEntry>> getFavorites(@AuthenticationPrincipal UserDetails principal) {
        User user = userService.findByUsername(principal.getUsername());
        return ResponseEntity.ok(watchlistService.getFavorites(user.getId()));
    }
}
