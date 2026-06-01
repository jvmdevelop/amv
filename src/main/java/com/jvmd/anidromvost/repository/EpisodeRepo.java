package com.jvmd.anidromvost.repository;

import com.jvmd.anidromvost.model.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepo extends JpaRepository<Episode, Long> {
    List<Episode> findByAnimeIdOrderByEpisodeNumber(Long animeId);

    Optional<Episode> findByAnimeIdAndEpisodeNumber(Long animeId, Integer episodeNumber);
}
