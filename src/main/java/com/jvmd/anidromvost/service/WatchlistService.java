package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.Anime;
import com.jvmd.anidromvost.model.User;
import com.jvmd.anidromvost.model.WatchlistEntry;
import com.jvmd.anidromvost.repository.AnimeRepo;
import com.jvmd.anidromvost.repository.WatchlistRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class WatchlistService {
    private WatchlistRepo watchlistRepo;
    private AnimeRepo animeRepo;

    public WatchlistEntry addToWatchlist(User user, Long animeId) {
        if (watchlistRepo.existsByUserIdAndAnimeId(user.getId(), animeId)) {
            throw new RuntimeException("Already in watchlist");
        }
        Anime anime = animeRepo.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime not found: " + animeId));
        WatchlistEntry entry = WatchlistEntry.builder()
                .user(user)
                .anime(anime)
                .build();
        return watchlistRepo.save(entry);
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long animeId) {
        watchlistRepo.deleteByUserIdAndAnimeId(userId, animeId);
    }

    public WatchlistEntry toggleFavorite(Long userId, Long animeId) {
        WatchlistEntry entry = watchlistRepo.findByUserIdAndAnimeId(userId, animeId)
                .orElseThrow(() -> new RuntimeException("Not in watchlist"));
        entry.setFavorite(!entry.isFavorite());
        return watchlistRepo.save(entry);
    }

    public List<WatchlistEntry> getWatchlist(Long userId) {
        return watchlistRepo.findByUserId(userId);
    }

    public List<WatchlistEntry> getFavorites(Long userId) {
        return watchlistRepo.findByUserIdAndFavoriteTrue(userId);
    }
}
