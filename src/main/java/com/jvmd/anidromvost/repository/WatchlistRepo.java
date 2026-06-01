package com.jvmd.anidromvost.repository;

import com.jvmd.anidromvost.model.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepo extends JpaRepository<WatchlistEntry, Long> {
    List<WatchlistEntry> findByUserId(Long userId);

    List<WatchlistEntry> findByUserIdAndFavoriteTrue(Long userId);

    Optional<WatchlistEntry> findByUserIdAndAnimeId(Long userId, Long animeId);

    boolean existsByUserIdAndAnimeId(Long userId, Long animeId);

    void deleteByUserIdAndAnimeId(Long userId, Long animeId);
}
